package org.sharedhealth.mci.repository;

import org.sharedhealth.mci.model.Catchment;
import org.sharedhealth.mci.model.DuplicatePatient;

import java.util.List;
import java.util.UUID;

public class DuplicatePatientRepository {
    public List<DuplicatePatient> findByCatchmentAndHealthId(Catchment catchment, String healthId) {
        return null;
    }

    public void create(List<DuplicatePatient> duplicates, UUID marker) {

    }

    public void update(String healthId, Catchment oldCatchment, List<DuplicatePatient> duplicates, UUID marker) {

    }

    public void retire(String healthId, UUID marker) {

    }
}
