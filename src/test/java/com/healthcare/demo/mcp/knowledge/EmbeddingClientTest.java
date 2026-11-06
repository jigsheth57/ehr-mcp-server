package com.healthcare.demo.mcp.knowledge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClient;

class EmbeddingClientTest {
  @Test
  void refusesWrongDimensionAndNonFiniteVectors() {
    var model = mock(EmbeddingModel.class);
    var client =
        new EmbeddingClient(model, RestClient.builder(), "fixture", "deployment-v1", "openai", "");
    when(model.embed("test")).thenReturn(new float[2]);
    assertThrows(IllegalStateException.class, () -> client.embed("test"));
    float[] invalid = new float[768];
    invalid[12] = Float.NaN;
    when(model.embed("test")).thenReturn(invalid);
    assertThrows(IllegalStateException.class, () -> client.embed("test"));
  }

  @Test
  void mismatchRefusesRetrievalBeforeCallingProvider() {
    var jdbc = mock(JdbcTemplate.class);
    var embeddings = mock(EmbeddingClient.class);
    when(embeddings.model()).thenReturn("fixture");
    when(embeddings.identity()).thenReturn("revision-2");
    when(embeddings.preprocessing()).thenReturn("nomic-search-prefix-v1");
    when(jdbc.queryForObject(
            anyString(),
            eq(Integer.class),
            eq("fixture"),
            eq("revision-2"),
            eq("nomic-search-prefix-v1")))
        .thenReturn(1);
    assertThrows(
        IllegalStateException.class,
        () -> new PolicyTools(jdbc, embeddings).searchPolicies("P101", "query"));
    verify(embeddings, never()).embed(anyString());
  }
}
