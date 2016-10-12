package org.sharedhealth.mci.repository;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;
import static org.sharedhealth.mci.util.Constants.*;

public class PatientFeedRepository {
    private final MappingManager mappingManager;
    private Mapper<PatientUpdateLog> updateLogMapper;

    public PatientFeedRepository(MappingManager mappingManager) {
        this.mappingManager = mappingManager;
        this.updateLogMapper = this.mappingManager.mapper(PatientUpdateLog.class);
    }

    public PatientUpdateLog findPatientUpdateLog(UUID marker) {
        Select select = select().from(CF_PATIENT_UPDATE_LOG);

        if (marker != null) {
            List<Integer> yearsSince = getYearsSince(marker);
            select.where(in(YEAR, yearsSince.toArray()));
            select.where(gt(EVENT_ID, marker));
        }
        String updateLogStmt = select.limit(1).toString();

        ResultSet resultSet = mappingManager.getSession().execute(updateLogStmt);
        Result<PatientUpdateLog> map = updateLogMapper.map(resultSet);
        return map.isExhausted() ? null : map.one();
    }

    private List<Integer> getYearsSince(UUID marker) {
        return IntStream.rangeClosed(getYearOf(marker), getCurrentYear())
                .boxed().collect(Collectors.toList());
    }

    private static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private static int getYearOf(UUID uuid) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(TimeUuidUtil.getTimeFromUUID(uuid));
        return cal.get(Calendar.YEAR);
    }

}
