package org.sharedhealth.mci.rule;


import org.sharedhealth.mci.model.DuplicatePatient;

import java.util.ArrayList;
import java.util.List;

public class DuplicatePatientRuleEngine {

    private List<DuplicatePatientRule> rules;

    public DuplicatePatientRuleEngine(List<DuplicatePatientRule> rules) {
        this.rules = rules;
    }

    public List<DuplicatePatient> apply(String healthId) {
        List<DuplicatePatient> duplicates = new ArrayList<>();
        for (DuplicatePatientRule rule : rules) {
            if (rule != null) {
                rule.apply(healthId, duplicates);
            }
        }
        return duplicates;
    }
}
