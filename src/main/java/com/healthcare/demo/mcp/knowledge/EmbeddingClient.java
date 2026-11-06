package com.healthcare.demo.mcp.knowledge;

import com.healthcare.demo.shared.Json;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.*;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/** Provider-neutral vectors with pinned model revision and explicit preprocessing compatibility. */
@Service
public class EmbeddingClient {
  public static final String PREPROCESSING = "nomic-search-prefix-v1";
  private final EmbeddingModel embeddings;
  private final String model, identity;

  public EmbeddingClient(
      EmbeddingModel embeddings,
      RestClient.Builder rest,
      @Value("${demo.embedding-model}") String model,
      @Value("${demo.embedding-identity:}") String configuredIdentity,
      @Value("${demo.provider:ollama}") String provider,
      @Value("${demo.ollama-url:http://127.0.0.1:11434}") String url) {
    this.embeddings = embeddings;
    this.model = model;
    if (!configuredIdentity.isBlank()) this.identity = configuredIdentity;
    else if ("ollama".equals(provider)) {
      var factory =
          new JdkClientHttpRequestFactory(
              HttpClient.newBuilder()
                  .connectTimeout(Duration.ofSeconds(3))
                  .followRedirects(HttpClient.Redirect.NEVER)
                  .build());
      factory.setReadTimeout(Duration.ofSeconds(8));
      var root =
          Json.MAPPER.readTree(
              rest.clone()
                  .baseUrl(url)
                  .requestFactory(factory)
                  .build()
                  .get()
                  .uri("/api/tags")
                  .retrieve()
                  .body(String.class));
      String digest = null;
      for (var item : root.path("models"))
        if (model.equals(item.path("name").asText())
            || (model + ":latest").equals(item.path("name").asText()))
          digest = item.path("digest").asText();
      if (digest == null || digest.isBlank())
        throw new IllegalStateException(
            "Embedding model revision unavailable; install model and rebuild index");
      this.identity = "ollama:" + model + "@" + digest;
    } else
      throw new IllegalArgumentException(
          "Explicit embedding identity required for gateway; rebuild index after model changes");
  }

  public float[] embed(String text) {
    float[] vector = embeddings.embed(text);
    if (vector.length != 768)
      throw new IllegalStateException(
          "Embedding model must produce 768 dimensions; rebuild index after a model change");
    for (float value : vector)
      if (!Float.isFinite(value))
        throw new IllegalStateException("Embedding provider returned non-finite values");
    return vector;
  }

  public String model() {
    return model;
  }

  public String identity() {
    return identity;
  }

  public String preprocessing() {
    return PREPROCESSING;
  }
}
