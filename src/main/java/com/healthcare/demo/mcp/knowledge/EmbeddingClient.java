package com.healthcare.demo.mcp.knowledge;

import com.healthcare.demo.shared.Json;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/** Embeddings stay local. Failure is reported; we never substitute invented vectors. */
@Service
public class EmbeddingClient {
  private final RestClient http;
  private final String model;

  public EmbeddingClient(
      @Value("${demo.ollama-url}") String url, @Value("${demo.embedding-model}") String model) {
    this.model = model;
    var factory =
        new JdkClientHttpRequestFactory(
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build());
    factory.setReadTimeout(Duration.ofSeconds(30));
    http = RestClient.builder().baseUrl(url).requestFactory(factory).build();
  }

  public float[] embed(String text) {
    var response =
        http.post()
            .uri("/api/embed")
            .body(Map.of("model", model, "input", text))
            .retrieve()
            .body(String.class);
    var values = Json.MAPPER.readTree(response).path("embeddings").path(0);
    if (values.size() != 768)
      throw new IllegalStateException(
          "Embedding model must produce 768 dimensions; rebuild the index after a model change");
    return Json.MAPPER.treeToValue(values, float[].class);
  }

  public String model() {
    return model;
  }
}
