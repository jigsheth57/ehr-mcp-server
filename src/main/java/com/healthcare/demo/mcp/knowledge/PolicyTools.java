package com.healthcare.demo.mcp.knowledge;

import com.healthcare.demo.shared.Contracts.*;
import com.healthcare.demo.shared.Json;
import java.util.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/** Exact filters run before both searches. Vector ranking is not a clinical confidence score. */
@Service
public class PolicyTools {
  private final JdbcTemplate jdbc;
  private final EmbeddingClient embeddings;

  public PolicyTools(JdbcTemplate jdbc, EmbeddingClient embeddings) {
    this.jdbc = jdbc;
    this.embeddings = embeddings;
  }

  @Tool(
      description =
          "Retrieve current authorized demonstration policy passages using configured embeddings"
              + " and keyword search. Returns sources and supported demo candidate identifiers.")
  public PolicySearch searchPolicies(String patientId, String query) {
    Integer incompatible =
        jdbc.queryForObject(
            "SELECT count(*) FROM knowledge.document WHERE scope='demo' AND"
                + " effective_from<=CURRENT_DATE AND (effective_to IS NULL OR"
                + " effective_to>=CURRENT_DATE) AND (embedding IS NULL OR embedding_model IS"
                + " DISTINCT FROM ? OR embedding_identity IS DISTINCT FROM ? OR"
                + " embedding_preprocessing IS DISTINCT FROM ?)",
            Integer.class,
            embeddings.model(),
            embeddings.identity(),
            embeddings.preprocessing());
    if (incompatible != null && incompatible > 0)
      throw new IllegalStateException(
          "Policy index identity or preprocessing mismatch; run the explicit reindex release task");
    String vector = Json.write(embeddings.embed("search_query: " + query));
    String sql =
        """
WITH eligible AS (
    SELECT * FROM knowledge.document
    WHERE scope='demo' AND effective_from<=CURRENT_DATE
    AND (effective_to IS NULL OR effective_to>=CURRENT_DATE)
    AND embedding IS NOT NULL AND embedding_model=? AND embedding_identity=? AND embedding_preprocessing=?
), semantic AS (
    SELECT id,row_number() OVER(ORDER BY embedding <=> CAST(? AS vector)) AS rank FROM eligible
), lexical AS (
    SELECT id,row_number() OVER(ORDER BY ts_rank(to_tsvector('english',body),plainto_tsquery('english',?)) DESC) AS rank
    FROM eligible WHERE to_tsvector('english',body) @@ plainto_tsquery('english',?)
)
SELECT d.*,coalesce(1.0/(60+s.rank),0)+coalesce(1.0/(60+l.rank),0) AS score
FROM eligible d LEFT JOIN semantic s USING(id) LEFT JOIN lexical l USING(id)
WHERE s.rank<=5 OR l.rank<=5 ORDER BY score DESC LIMIT 5
""";
    var passages =
        jdbc.query(
            sql,
            (rs, n) ->
                new Evidence(
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getString("version"),
                    rs.getString("section"),
                    rs.getString("body"),
                    List.of(Json.read(rs.getString("candidates"), String[].class))),
            embeddings.model(),
            embeddings.identity(),
            embeddings.preprocessing(),
            vector,
            query,
            query);
    if (passages.isEmpty())
      throw new IllegalStateException("No current authorized indexed policy evidence is available");
    return new PolicySearch(
        passages, "PostgreSQL full-text + pgvector, reciprocal-rank fusion", embeddings.model());
  }
}
