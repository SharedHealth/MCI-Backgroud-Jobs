package org.sharedhealth.mci.mapper;

import org.sharedhealth.mci.model.Catchment;
import org.sharedhealth.mci.model.DuplicatePatient;
import org.sharedhealth.mci.model.Patient;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class DuplicatePatientMapper {
    public List<DuplicatePatient> mapToDuplicatePatientData(Patient patient1, Patient patient2, Set<String> reasons) {
        List<DuplicatePatient> duplicates = new ArrayList<>();
        Catchment catchment1 = patient1.getCatchment();
        Catchment catchment2 = patient2.getCatchment();
        if (catchment1.equals(catchment2)) {
            buildDuplicates(catchment1, patient1.getHealthId(), patient2.getHealthId(), reasons, duplicates);
        } else {
            buildDuplicates(catchment1, patient1.getHealthId(), patient2.getHealthId(), reasons, duplicates);
            buildDuplicates(catchment2, patient2.getHealthId(), patient1.getHealthId(), reasons, duplicates);
        }
        return duplicates;
    }

    private void buildDuplicates(Catchment catchment, String healthId1, String healthId2, Set<String> reasons,
                                 List<DuplicatePatient> duplicates) {
        for (String catchmentId : catchment.getAllIds()) {
            DuplicatePatient duplicate = new DuplicatePatient(catchmentId, healthId1, healthId2, reasons, TimeUuidUtil.uuidForDate(new Date()));
            duplicates.add(duplicate);
        }
    }
}
