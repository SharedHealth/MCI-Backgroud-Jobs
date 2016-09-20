package org.sharedhealth.mci.repository;

import com.datastax.driver.core.querybuilder.Select;
import org.sharedhealth.mci.model.Patient;

import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.sharedhealth.mci.util.Constants.*;

public class PatientRepository {
    public Patient findByHealthId(String healthId) {
        return null;
    }

    public List<Patient> findAllByQuery(String key, String value, String tableName) {
        Select.Where where = select(HEALTH_ID).from(tableName).where(eq(key, value));
        return null;
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
        return null;
    }
}
