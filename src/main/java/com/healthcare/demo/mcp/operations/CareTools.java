package com.healthcare.demo.mcp.operations;

import com.healthcare.demo.mcp.safety.SafetyService;
import com.healthcare.demo.shared.Contracts.CareOptions;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CareTools {
  private final JdbcTemplate jdbc;

  public CareTools(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Tool(
      description =
          "Read fictional formulary coverage and available follow-up options. Does not prescribe or"
              + " book anything.")
  public CareOptions getCareOptions(String patientId, String proposedTreatment) {
    String drug = SafetyService.normalize(proposedTreatment);
    var plans =
        jdbc.queryForList(
            "SELECT plan_id FROM clinical.patient WHERE patient_id=?", String.class, patientId);
    if (plans.isEmpty() || drug == null)
      throw new IllegalArgumentException("Patient or medication is not supported");
    var coverage =
        jdbc.queryForList(
            "SELECT covered FROM operations.coverage_rule WHERE plan_id=? AND drug=?",
            Boolean.class,
            plans.get(0),
            drug);
    if (coverage.isEmpty()) throw new IllegalStateException("Coverage record is missing");
    var alternatives =
        jdbc.queryForList(
            "SELECT drug FROM operations.coverage_rule WHERE plan_id=? AND covered=true ORDER BY"
                + " drug",
            String.class,
            plans.get(0));
    var slots =
        jdbc.queryForList(
            "SELECT label FROM operations.followup_slot WHERE available=true ORDER BY id",
            String.class);
    return new CareOptions(coverage.get(0), alternatives, slots, "synthetic-formulary-v1");
  }
}
