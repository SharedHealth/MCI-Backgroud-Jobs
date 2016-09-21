package org.sharedhealth.mci.repository;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.sharedhealth.mci.model.Catchment;
import org.sharedhealth.mci.model.DuplicatePatient;
import org.sharedhealth.mci.model.Marker;
import org.sharedhealth.mci.model.Patient;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.sharedhealth.mci.util.Constants.*;

public class DuplicatePatientRepository {
    private Mapper<DuplicatePatient> duplicatePatientMapper;
    private Mapper<Patient> patientMapper;
    private Mapper<Marker> markerMapper;
    private Session session;

    public DuplicatePatientRepository(MappingManager mappingManager) {
        session = mappingManager.getSession();
        this.duplicatePatientMapper = mappingManager.mapper(DuplicatePatient.class);
        this.markerMapper = mappingManager.mapper(Marker.class);
        this.patientMapper = mappingManager.mapper(Patient.class);
    }

    public List<DuplicatePatient> findByCatchmentAndHealthId(Catchment catchment, String healthId) {
        return null;
    }

    public void create(List<DuplicatePatient> duplicates, UUID marker) {
        BatchStatement batch = new BatchStatement();
        for (DuplicatePatient duplicate : duplicates) {
            batch.add(duplicatePatientMapper.saveQuery(duplicate));
        }
        Statement saveMarkerStatement = buildInsertMarkerStatement(marker);
        batch.add(saveMarkerStatement);
        session.execute(batch);
    }

    public void update(String healthId, Catchment oldCatchment, List<DuplicatePatient> duplicates, UUID marker) {

    }

    public void retire(String retiredHealthId, UUID marker) {
        Patient patient = patientMapper.get(retiredHealthId);
        Catchment catchment = patient.getCatchment();

        List<DuplicatePatient> duplicates = findDuplicateByHealthIdAndCatchment(retiredHealthId, catchment);
        duplicates.addAll(findDuplicatesHavingHID2AsRetiredHID(retiredHealthId, duplicates));

        BatchStatement batch = new BatchStatement();
        for (DuplicatePatient duplicate : duplicates) {
            batch.add(duplicatePatientMapper.deleteQuery(duplicate));
        }
        Statement insertMarkerStatement = buildInsertMarkerStatement(marker);
        batch.add(insertMarkerStatement);
        session.execute(batch);
    }

    private List<DuplicatePatient> findDuplicatesHavingHID2AsRetiredHID(String retiredHealthId, List<DuplicatePatient> duplicates) {
        List<DuplicatePatient> duplicatesHavingRetiredHID = new ArrayList<>();
        for (DuplicatePatient duplicate : duplicates) {
            Patient secondPatient = patientMapper.get(duplicate.getHealthId2());
            for (String catchmentId : secondPatient.getCatchment().getAllIds()) {
                Select.Where selectDuplicate = select().from(CF_PATIENT_DUPLICATE).allowFiltering()
                        .where(eq(CATCHMENT_ID, catchmentId))
                        .and(eq(HEALTH_ID1, duplicate.getHealthId2()))
                        .and(eq(HEALTH_ID2, retiredHealthId));
                duplicatesHavingRetiredHID.addAll(duplicatePatientMapper.map(session.execute(selectDuplicate)).all());
            }
        }
        return duplicatesHavingRetiredHID;
    }

    private List<DuplicatePatient> findDuplicateByHealthIdAndCatchment(String healthId, Catchment catchment) {
        List<DuplicatePatient> duplicates = new ArrayList<>();
        for (String catchmentId : catchment.getAllIds()) {
            Select.Where selectDuplicates = select().from(CF_PATIENT_DUPLICATE)
                    .where(eq(CATCHMENT_ID, catchmentId))
                    .and(eq(HEALTH_ID1, healthId));
            duplicates.addAll(duplicatePatientMapper.map(session.execute(selectDuplicates)).all());
        }
        return duplicates;
    }

    private Statement buildInsertMarkerStatement(UUID marker) {
        Marker markerToSave = new Marker();
        markerToSave.setType(DUPLICATE_PATIENT_MARKER);
        markerToSave.setValue(marker.toString());
        markerToSave.setCreatedAt(TimeUuidUtil.uuidForDate(new Date()));
        return markerMapper.saveQuery(markerToSave);
    }
}
