package org.sharedhealth.mci.repository;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.sharedhealth.mci.model.Patient;

import java.util.List;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.sharedhealth.mci.util.Constants.*;

public class PatientRepository {
    private Mapper<Patient> patientMapper;
    private Session session;

    public PatientRepository(MappingManager mappingManager) {
        this.patientMapper = mappingManager.mapper(Patient.class);
        this.session = mappingManager.getSession();
    }

    public Patient findByHealthId(String healthId) {
        return patientMapper.get(healthId);
    }

    public List<Patient> findAllByQuery(String key, String value, String tableName) {
        ResultSet resultSet = session.execute(select(HEALTH_ID).from(tableName).where(eq(key, value)));
        return findAllByHIDRows(resultSet.all());
    }

    public List<Patient> findAllByNameAndAddress(String givenName, String surName, String divisionId,
                                                 String districtId, String upazilaId) {
        Select.Where findByAddress = select(HEALTH_ID).from(CF_NAME_MAPPING)
                .where(eq(DIVISION_ID, divisionId))
                .and(eq(DISTRICT_ID, districtId))
                .and(eq(UPAZILA_ID, upazilaId))
                .and(eq(GIVEN_NAME, givenName.toLowerCase()));

        if (isNotEmpty(surName)) {
            findByAddress = findByAddress.and(eq(SUR_NAME, surName.toLowerCase()));
        }

        ResultSet resultSet = session.execute(findByAddress);
        return findAllByHIDRows(resultSet.all());
    }

    private List<Patient> findAllByHIDRows(List<Row> rowsWithHID) {
        return rowsWithHID.stream().map(row -> findByHealthId(row.getString(HEALTH_ID))).collect(Collectors.toList());
    }
}
