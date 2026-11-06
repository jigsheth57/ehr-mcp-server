package com.healthcare.demo.mcp;

import static org.assertj.core.api.Assertions.*;

import com.healthcare.demo.mcp.patient.PatientStore;
import com.healthcare.demo.mcp.safety.SafetyService;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

class SafetyServiceTest {
  JdbcTemplate jdbc;
  SafetyService safety;

  @BeforeEach
  void records() {
    jdbc =
        new JdbcTemplate(
            new DriverManagerDataSource(
                "jdbc:h2:mem:"
                    + java.util.UUID.randomUUID()
                    + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE",
                "sa",
                ""));
    jdbc.execute("CREATE SCHEMA clinical");
    jdbc.execute(
        "CREATE TABLE clinical.patient(patient_id varchar primary key, name varchar, age int,"
            + " version bigint, allergy_status varchar, preference varchar, scenario varchar,"
            + " cohort varchar)");
    jdbc.execute(
        "CREATE TABLE clinical.allergy(id varchar, patient_id varchar, substance varchar, reaction"
            + " varchar, verification varchar)");
    jdbc.execute(
        "CREATE TABLE clinical.medication(id varchar, patient_id varchar, drug varchar, regimen"
            + " varchar, status varchar)");
    jdbc.execute("CREATE TABLE clinical.condition(patient_id varchar, name varchar)");
    jdbc.execute(
        "CREATE TABLE clinical.observation(patient_id varchar, name varchar, value varchar, unit"
            + " varchar, recorded_at varchar)");
    jdbc.execute(
        "CREATE TABLE clinical.encounter(id varchar, patient_id varchar, visit_date varchar, reason"
            + " varchar, status varchar)");
    jdbc.update(
        "INSERT INTO clinical.patient VALUES('P101','Synthetic"
            + " One',45,7,'REVIEWED','Evening','allergy','test')");
    jdbc.update(
        "INSERT INTO clinical.allergy VALUES('A-17','P101','Penicillin','Unknown','CONFIRMED')");
    safety = new SafetyService(new PatientStore(jdbc));
  }

  @Test
  void aKnownAllergyBlocksTheProposal() {
    var result = safety.evaluate("P101", "Amoxicillin 500mg TID", List.of());
    assertThat(result.supported()).isTrue();
    assertThat(result.findings())
        .anyMatch(f -> f.severity().equals("BLOCKING") && f.sourceId().equals("A-17"));
  }

  @Test
  void exactDrugAllergiesAreCheckedBeyondTheOriginalFiveRules() {
    jdbc.update(
        "INSERT INTO clinical.allergy VALUES('A-18','P101','Codeine','Unknown','CONFIRMED')");
    assertThat(safety.evaluate("P101", "Codeine 15mg", List.of()).findings())
        .anyMatch(f -> f.severity().equals("BLOCKING"));
  }

  @Test
  void combinedOrAmbiguousProposalsAreNotPartiallyChecked() {
    assertThat(
            safety.evaluate("P101", "Acetaminophen and ibuprofen", List.of("Apixaban")).supported())
        .isFalse();
    assertThat(safety.evaluate("P101", "Acetaminophen with MadeupDrug", List.of()).supported())
        .isFalse();
    assertThat(safety.evaluate("P101", "Augmentin", List.of()).supported()).isFalse();
  }

  @Test
  void anUnsupportedDrugDoesNotBecomeAnApproval() {
    assertThat(safety.evaluate("P101", "MadeupDrug 100mg", List.of()).supported()).isFalse();
  }

  @Test
  void aMissingPatientIsNotAnEmptyHealthyRecord() {
    assertThatThrownBy(() -> safety.evaluate("MISSING", "Amoxicillin", List.of()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void reportedMedicationIsIncludedInTheRecheck() {
    assertThat(safety.evaluate("P101", "Ibuprofen 200mg", List.of("Apixaban")).findings())
        .anyMatch(f -> f.code().equals("INTERACTION") && f.severity().equals("BLOCKING"));
  }
}
