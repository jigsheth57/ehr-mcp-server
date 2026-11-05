package com.healthcare.demo.mcp;

import com.healthcare.demo.mcp.entity.PatientEntity;
import com.healthcare.demo.mcp.repository.PatientRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EhrTools {

    public record PatientRecord(
        String patientId,
        String fullName,
        int age,
        String gender,
        List<String> allergies,
        List<String> activeConditions,
        List<String> currentMedications
    ) {}

    private final PatientRepository patientRepository;

    public EhrTools(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Tool(description = "Fetch real-time patient Electronic Health Record (EHR) including known drug allergies, active diagnoses, and current medications from Postgres.")
    public PatientRecord getPatientEhr(@ToolParam(description = "Unique patient ID, e.g. P101 through P120") String patientId) {
        Optional<PatientEntity> patientOpt = patientRepository.findById(patientId);

        if (patientOpt.isEmpty()) {
            return new PatientRecord(patientId, "UNKNOWN PATIENT", 0, "UNKNOWN", List.of(), List.of(), List.of());
        }

        PatientEntity p = patientOpt.get();
        return new PatientRecord(
            p.getPatientId(),
            p.getFirstName() + " " + p.getLastName(),
            p.getAge(),
            p.getGender(),
            p.getAllergies(),
            p.getActiveConditions(),
            p.getCurrentMedications()
        );
    }

    @Tool(description = "Check for clinical contraindications between a proposed medication and a patient's documented allergies or active medications.")
    public String checkDrugInteraction(
            @ToolParam(description = "Proposed medication name and dose") String proposedDrug,
            @ToolParam(description = "Known patient allergy or active drug from EHR") String existingEntity) {
        
        String drug = proposedDrug.toLowerCase();
        String existing = existingEntity.toLowerCase();

        // 1. Beta-lactam / Penicillin Cross-Reactivity
        if ((drug.contains("amoxicillin") || drug.contains("ampicillin") || drug.contains("augmentin")) && 
            (existing.contains("penicillin") || existing.contains("amoxicillin"))) {
            return "CRITICAL ALERT: " + proposedDrug + " is a beta-lactam penicillin. Severe risk of immediate anaphylaxis!";
        }

        // 2. Macrolide Allergy Cross-Reactivity (e.g., P117)
        if ((drug.contains("azithromycin") || drug.contains("clarithromycin") || drug.contains("erythromycin")) && 
            existing.contains("macrolide")) {
            return "CRITICAL ALERT: Patient is allergic to Macrolide antibiotics. " + proposedDrug + " is contraindicated.";
        }

        // 3. Fluoroquinolone Cross-Reactivity (e.g., P105)
        if ((drug.contains("ciprofloxacin") || drug.contains("levofloxacin")) && 
            (existing.contains("ciprofloxacin") || existing.contains("fluoroquinolone"))) {
            return "CRITICAL ALERT: Patient has documented severe adverse reaction to Fluoroquinolones.";
        }

        // 4. ACE Inhibitor / ARB Cross-Reaction (e.g., P108)
        if (drug.contains("lisinopril") && existing.contains("ace inhibitor")) {
            return "CRITICAL ALERT: Documented history of ACE-inhibitor induced angioedema.";
        }

        // 5. Anticoagulant Interaction (NSAID + Blood Thinner)
        if (drug.contains("ibuprofen") && (existing.contains("apixaban") || existing.contains("rivaroxaban"))) {
            return "WARNING: Major risk of gastrointestinal hemorrhage combining NSAIDs with direct oral anticoagulants.";
        }

        return "No severe contraindications flagged between '" + proposedDrug + "' and '" + existingEntity + "'.";
    }
}