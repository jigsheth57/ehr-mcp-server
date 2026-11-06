package com.healthcare.demo.mcp.safety;

import com.healthcare.demo.mcp.patient.PatientStore;
import com.healthcare.demo.shared.Contracts.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

/** Small, inspectable demonstration rules. This is not a comprehensive drug knowledge base. */
@Service
public class SafetyService {
    public static final String RULE_VERSION = "demo-rules-v1";
    public static final List<String> DRUGS = List.of("trimethoprim-sulfamethoxazole", "acetaminophen", "clarithromycin", "azithromycin", "amoxicillin", "ampicillin", "doxycycline", "cephalexin", "levofloxacin", "ciprofloxacin", "ibuprofen", "lisinopril", "metformin", "codeine");
    private final PatientStore patients;
    public SafetyService(PatientStore patients) { this.patients = patients; }

    public SafetyCheck evaluate(String patientId, String proposedTreatment, List<String> reportedMedications) {
        var patient = patients.find(patientId);
        var findings = new ArrayList<Finding>();
        String drug = normalize(proposedTreatment);
        if (drug == null) return new SafetyCheck(false, "UNKNOWN", List.of(new Finding("UNSUPPORTED_DRUG","REVIEW","This medication is outside the demonstration rule set.",RULE_VERSION)),List.of(),RULE_VERSION);
        if ("UNKNOWN".equals(patient.allergyStatus())) findings.add(new Finding("MISSING_ALLERGY_REVIEW","MISSING","The allergy history has not been reviewed.",patient.snapshotVersion()));
        for (var allergy : patient.allergies()) {
            if (matchesAllergy(drug, allergy.substance())) findings.add(new Finding("ALLERGY","BLOCKING","The proposed "+drug+" matches a documented allergy or a covered drug-class rule.",allergy.id()));
        }
        var medicines = new ArrayList<>(patient.medications().stream().map(Medication::name).toList());
        if (reportedMedications != null) medicines.addAll(reportedMedications);
        for (String current : medicines) {
            String lower = current.toLowerCase(Locale.ROOT);
            if (drug.equals("ibuprofen") && (lower.contains("apixaban") || lower.contains("rivaroxaban")))
                findings.add(new Finding("INTERACTION","BLOCKING","The demo rule flags ibuprofen with this anticoagulant.","medication-history"));
        }
        var evidence = new Evidence(RULE_VERSION,"Demonstration medication checks",RULE_VERSION,"scope",
            "Checks exact allergies, selected antibiotic families, and the NSAID/anticoagulant fixture. Does not verify diagnosis, dose, renal adjustment, or all possible interactions.",List.of());
        return new SafetyCheck(findings.stream().noneMatch(f->f.severity().equals("MISSING")),drug,List.copyOf(findings),List.of(evidence),RULE_VERSION);
    }
    public static String normalize(String treatment) {
        if (treatment == null) return null;
        String lower = treatment.toLowerCase(Locale.ROOT).replace("augmentin","amoxicillin").replace("tylenol","acetaminophen").replace("bactrim","trimethoprim-sulfamethoxazole");
        for (String drug : DRUGS) if (Pattern.compile("(?<![a-z])"+Pattern.quote(drug)+"(?![a-z])").matcher(lower).find()) return drug;
        return null;
    }
    private boolean matchesAllergy(String drug, String substance) {
        String allergy = substance.toLowerCase(Locale.ROOT);
        if (allergy.contains(drug)) return true;
        return (Set.of("amoxicillin","ampicillin").contains(drug) && allergy.contains("penicillin")) ||
            (Set.of("azithromycin","clarithromycin").contains(drug) && allergy.contains("macrolide")) ||
            (Set.of("levofloxacin","ciprofloxacin").contains(drug) && allergy.contains("fluoroquinolone")) ||
            (drug.equals("lisinopril") && allergy.contains("ace inhibitor")) ||
            (drug.equals("trimethoprim-sulfamethoxazole") && (allergy.contains("sulfa") || allergy.contains("sulfamethoxazole")));
    }
}
