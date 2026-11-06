package com.healthcare.demo.mcp.knowledge;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.healthcare.demo.shared.Json;
import java.time.LocalDate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/** Isolated pgvector test: real SQL filtering/ranking and fail-closed index compatibility. */
@EnabledIfEnvironmentVariable(named = "DEMO_CONTAINER_TEST", matches = "true")
@Testcontainers
class PostgresPolicyTest {
  @Container
  static final PostgreSQLContainer database =
      new PostgreSQLContainer(
          DockerImageName.parse("pgvector/pgvector:pg17").asCompatibleSubstituteFor("postgres"));

  static JdbcTemplate jdbc;
  EmbeddingClient embeddings;
  PolicyTools tools;

  @BeforeAll
  static void migrate() {
    var ds =
        new DriverManagerDataSource(
            database.getJdbcUrl(), database.getUsername(), database.getPassword());
    for (String name : new String[] {"V1", "V2", "V3", "V4", "V5"}) {
      // Resolve exact packaged migration names, keeping applied migrations immutable.
      try {
        var resolver =
            new org.springframework.core.io.support.PathMatchingResourcePatternResolver();
        var resources = resolver.getResources("classpath*:db/demo/" + name + "__*.sql");
        assertThat(resources).hasSize(1);
        new ResourceDatabasePopulator(resources).execute(ds);
      } catch (java.io.IOException e) {
        throw new IllegalStateException(e);
      }
    }
    jdbc = new JdbcTemplate(ds);
  }

  @BeforeEach
  void fixtures() {
    jdbc.update("DELETE FROM knowledge.document");
    embeddings = mock(EmbeddingClient.class);
    when(embeddings.model()).thenReturn("fixture");
    when(embeddings.identity()).thenReturn("fixture@digest");
    when(embeddings.preprocessing()).thenReturn(EmbeddingClient.PREPROCESSING);
    float[] vector = new float[768];
    vector[0] = 1;
    when(embeddings.embed(anyString())).thenReturn(vector);
    tools = new PolicyTools(jdbc, embeddings);
    insert("current", "demo", LocalDate.now().minusDays(1), null, "fixture@digest", vector);
    insert("private", "private", LocalDate.now().minusDays(1), null, "fixture@digest", vector);
    insert(
        "expired",
        "demo",
        LocalDate.now().minusDays(2),
        LocalDate.now().minusDays(1),
        "fixture@digest",
        vector);
    insert("future", "demo", LocalDate.now().plusDays(1), null, "fixture@digest", vector);
  }

  void insert(
      String id, String scope, LocalDate from, LocalDate to, String identity, float[] vector) {
    jdbc.update(
        "INSERT INTO"
            + " knowledge.document(id,title,version,section,body,candidates,scope,effective_from,effective_to,checksum,embedding_model,source_file,embedding,embedding_identity,embedding_preprocessing)"
            + " VALUES(?,?,'v1','fixture','fictional policy"
            + " medicine','[]',?,?,?,'checksum','fixture','test',CAST(? AS vector),?,?)",
        id,
        id,
        scope,
        from,
        to,
        Json.write(vector),
        identity,
        EmbeddingClient.PREPROCESSING);
  }

  @Test
  void vectorAndLexicalSearchApplyEligibilityBeforeRanking() {
    assertThat(tools.searchPolicies("P101", "fictional medicine").passages())
        .extracting(e -> e.id())
        .containsExactly("current");
    verify(embeddings).embed("search_query: fictional medicine");
  }

  @Test
  void changedIdentityRefusesRetrievalUntilExplicitReindex() {
    jdbc.update("UPDATE knowledge.document SET embedding_identity='different' WHERE id='current'");
    assertThatThrownBy(() -> tools.searchPolicies("P101", "medicine"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("reindex");
    verify(embeddings, never()).embed(anyString());
  }
}
