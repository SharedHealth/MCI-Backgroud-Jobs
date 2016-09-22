package org.sharedhealth.mci.repository;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.sharedhealth.mci.model.*;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.mapping.Mapper.Option.timestamp;
import static java.lang.System.currentTimeMillis;
import static org.sharedhealth.mci.util.Constants.*;

public class DuplicatePatientRepository {
    private Mapper<DuplicatePatient> duplicatePatientMapper;
    private Mapper<DuplicatePatientIgnored> duplicateIgnoreMapper;
    private PatientRepository patientRepository;
    private Mapper<Marker> markerMapper;
    private Session session;

    public static final int BATCH_QUERY_EXEC_DELAY = 100;

    public DuplicatePatientRepository(MappingManager mappingManager, PatientRepository patientRepository) {
        session = mappingManager.getSession();
        this.duplicatePatientMapper = mappingManager.mapper(DuplicatePatient.class);
        this.markerMapper = mappingManager.mapper(Marker.class);
        this.duplicateIgnoreMapper = mappingManager.mapper(DuplicatePatientIgnored.class);
        this.patientRepository = patientRepository;
    }

    public List<DuplicatePatient> findByCatchmentAndHealthId(Catchment catchment, String healthId) {
        List<DuplicatePatient> duplicates = new ArrayList<>();
        for (String catchmentId : catchment.getAllIds()) {
            Select.Where selectDuplicates = buildSelectDuplicateByHIDAndCatchment(healthId, catchmentId);
            duplicates.addAll(duplicatePatientMapper.map(session.execute(selectDuplicates)).all());
        }
        return duplicates;
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
        Patient patient = patientRepository.findByHealthId(healthId);
        if (oldCatchment == null) {
            oldCatchment = patient.getCatchment();
        }
        long currentTimestamp = getCurrentTimeInMicros();
        BatchStatement batch = new BatchStatement();
        buildRetireDuplicateBatch(healthId, oldCatchment, batch, currentTimestamp);

        List<DuplicatePatient> ignoredFilteredDuplicates = duplicates.stream().filter(duplicate ->
                        null == duplicateIgnoreMapper.get(duplicate.getHealthId1(), duplicate.getHealthId2())
        ).collect(Collectors.toList());

        ignoredFilteredDuplicates.stream().forEach(duplicate ->
                batch.add(duplicatePatientMapper.saveQuery(duplicate, timestamp(currentTimestamp + BATCH_QUERY_EXEC_DELAY))));

        batch.add(buildInsertMarkerStatement(marker));
        session.execute(batch);
    }

    public void retire(String retiredHealthId, UUID marker) {
        BatchStatement batch = new BatchStatement();
        Patient patient = patientRepository.findByHealthId(retiredHealthId);
        Catchment catchment = patient.getCatchment();

        buildRetireDuplicateBatch(retiredHealthId, catchment, batch, getCurrentTimeInMicros());
        batch.add(buildInsertMarkerStatement(marker));
        session.execute(batch);
    }

    private void buildRetireDuplicateBatch(String retiredHealthId, Catchment catchment, BatchStatement batch, long timestamp) {
        List<DuplicatePatient> duplicates = findDuplicateByHealthIdAndCatchment(retiredHealthId, catchment);
        duplicates.addAll(findDuplicatesHavingHID2AsRetiredHID(retiredHealthId, duplicates));

        for (DuplicatePatient duplicate : duplicates) {
            Statement deleteQuery = duplicatePatientMapper.deleteQuery(duplicate, timestamp(timestamp));
            batch.add(deleteQuery);
        }
    }

    private List<DuplicatePatient> findDuplicatesHavingHID2AsRetiredHID(String retiredHealthId, List<DuplicatePatient> duplicates) {
        List<DuplicatePatient> duplicatesHavingRetiredHID = new ArrayList<>();
        for (DuplicatePatient duplicate : duplicates) {
            Patient secondPatient = patientRepository.findByHealthId(duplicate.getHealthId2());
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
            Select.Where selectDuplicates = buildSelectDuplicateByHIDAndCatchment(healthId, catchmentId);
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

    private Select.Where buildSelectDuplicateByHIDAndCatchment(String healthId, String catchmentId) {
        return select().from(CF_PATIENT_DUPLICATE)
                .where(eq(CATCHMENT_ID, catchmentId))
                .and(eq(HEALTH_ID1, healthId));
    }

    private long getCurrentTimeInMicros() {
        return currentTimeMillis() * 1000;
    }
}
