package com.healthcare.demo.mcp;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {
  private final JdbcTemplate jdbc;

  public HealthController(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of(
        "service", "ehr-mcp-server", "database", jdbc.queryForObject("SELECT 1", Integer.class));
  }
}
