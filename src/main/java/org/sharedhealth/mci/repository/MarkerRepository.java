package org.sharedhealth.mci.repository;


import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.MappingManager;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.sharedhealth.mci.util.Constants.*;

public class MarkerRepository {
    private final MappingManager mappingManager;

    public MarkerRepository(MappingManager mappingManager) {
        this.mappingManager = mappingManager;
    }

    public String find(String markerType) {
        String cql = select().from(CF_MARKER).where(eq(TYPE, markerType)).limit(1).toString();
        ResultSet rs = mappingManager.getSession().execute(cql);
        return rs.isExhausted() ? null : rs.one().getString(MARKER);
    }
}
