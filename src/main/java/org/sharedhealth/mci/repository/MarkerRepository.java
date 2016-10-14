package org.sharedhealth.mci.repository;


import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.MappingManager;
import org.sharedhealth.mci.model.Marker;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.Date;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;
import static com.datastax.driver.mapping.Mapper.Option.timestamp;
import static java.lang.System.currentTimeMillis;
import static org.sharedhealth.mci.util.Constants.*;

public class MarkerRepository {
    private static final long QUERY_EXEC_DELAY = 100;
    private final MappingManager mappingManager;

    public MarkerRepository(MappingManager mappingManager) {
        this.mappingManager = mappingManager;
    }

    public void save(String type, String value) {
        BatchStatement batchStatement = buildUpdateMarkerBatch(type, value);
        mappingManager.getSession().execute(batchStatement);
    }

    public String find(String markerType) {
        String cql = select().from(CF_MARKER).where(eq(TYPE, markerType)).limit(1).toString();
        ResultSet rs = mappingManager.getSession().execute(cql);
        return rs.isExhausted() ? null : rs.one().getString(MARKER);
    }

    private BatchStatement buildUpdateMarkerBatch(String type, String value) {
        BatchStatement batch = new BatchStatement();
        long timeInMicros = currentTimeMillis() * 1000;

        Delete delete = delete().from(CF_MARKER);
        delete.where(eq(TYPE, type));
        delete.using(QueryBuilder.timestamp(timeInMicros));
        batch.add(delete);

        Marker newMarker = new Marker();
        newMarker.setType(type);
        newMarker.setCreatedAt(TimeUuidUtil.uuidForDate(new Date()));
        newMarker.setValue(value);
        Statement insert = mappingManager.mapper(Marker.class).saveQuery(newMarker, timestamp(timeInMicros + QUERY_EXEC_DELAY));
        batch.add(insert);
        return batch;
    }

}
