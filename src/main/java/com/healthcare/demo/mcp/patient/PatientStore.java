package com.healthcare.demo.mcp.patient;

import com.healthcare.demo.shared.Contracts.*;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Read the chart as exact database facts. A missing chart is an error, not a healthy patient. */
@Repository
public class PatientStore {
  private final JdbcTemplate jdbc;

  public PatientStore(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public PatientSnapshot find(String patientId) {
    var rows =
        jdbc.query(
            "SELECT * FROM clinical.patient WHERE patient_id = ?",
            (rs, n) ->
                new PatientSnapshot(
                    rs.getString("patient_id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    "EHR-v" + rs.getLong("version"),
                    rs.getString("allergy_status"),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    rs.getString("preference")),
            patientId);
    if (rows.isEmpty()) throw new IllegalArgumentException("Patient record not found");
    var p = rows.get(0);
    return new PatientSnapshot(
        p.patientId(),
        p.name(),
        p.age(),
        p.snapshotVersion(),
        p.allergyStatus(),
        jdbc.query(
            "SELECT * FROM clinical.allergy WHERE patient_id=? ORDER BY id",
            (rs, n) ->
                new Allergy(
                    rs.getString("id"),
                    rs.getString("substance"),
                    rs.getString("reaction"),
                    rs.getString("verification")),
            patientId),
        jdbc.query(
            "SELECT * FROM clinical.medication WHERE patient_id=? AND status='ACTIVE' ORDER BY id",
            (rs, n) ->
                new Medication(
                    rs.getString("id"),
                    rs.getString("drug"),
                    rs.getString("regimen"),
                    rs.getString("status")),
            patientId),
        jdbc.query(
            "SELECT name FROM clinical.condition WHERE patient_id=?",
            (rs, n) -> rs.getString(1),
            patientId),
        jdbc.query(
            "SELECT * FROM clinical.observation WHERE patient_id=? ORDER BY recorded_at DESC LIMIT"
                + " 8",
            (rs, n) ->
                new Observation(
                    rs.getString("name"),
                    rs.getString("value"),
                    rs.getString("unit"),
                    rs.getString("recorded_at")),
            patientId),
        jdbc.query(
            "SELECT * FROM clinical.encounter WHERE patient_id=? ORDER BY visit_date DESC LIMIT 5",
            (rs, n) ->
                new Encounter(
                    rs.getString("id"),
                    rs.getString("visit_date"),
                    rs.getString("reason"),
                    rs.getString("status")),
            patientId),
        p.preference());
  }

  public List<PatientSummary> list() {
    return jdbc.query(
        "SELECT patient_id,name,age,scenario FROM clinical.patient WHERE cohort='hospital-demo-v1'"
            + " ORDER BY patient_id",
        (rs, n) ->
            new PatientSummary(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4)));
  }
}
