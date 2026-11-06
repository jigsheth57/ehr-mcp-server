package com.healthcare.demo.mcp;

import com.healthcare.demo.mcp.patient.PatientStore;
import com.healthcare.demo.mcp.safety.SafetyService;
import com.healthcare.demo.shared.Contracts.*;
import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/** The MCP adapter is deliberately thin; ordinary services hold the readable business code. */
@Service
public class EhrTools {
  private final PatientStore patients;
  private final SafetyService safety;

  public EhrTools(PatientStore patients, SafetyService safety) {
    this.patients = patients;
    this.safety = safety;
  }

  @Tool(description = "List the synthetic demonstration patients, without their medical histories.")
  public List<PatientSummary> listDemoPatients() {
    return patients.list();
  }

  @Tool(description = "Read the exact authorized patient chart. A missing patient is an error.")
  public PatientSnapshot getPatientSnapshot(String patientId) {
    return patients.find(patientId);
  }

  @Tool(
      description =
          "Run the documented demonstration allergy and selected medication-interaction checks."
              + " This does not establish clinical safety or dose correctness.")
  public SafetyCheck evaluateMedicationSafety(
      String patientId, String proposedTreatment, List<String> reportedMedications) {
    return safety.evaluate(patientId, proposedTreatment, reportedMedications);
  }
}
