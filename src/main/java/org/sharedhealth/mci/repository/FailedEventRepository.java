package org.sharedhealth.mci.repository;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.sharedhealth.mci.model.FailedEvent;

import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.sharedhealth.mci.util.Constants.CF_FAILED_EVENTS;
import static org.sharedhealth.mci.util.Constants.FAILURE_TYPE;

public class FailedEventRepository {
    private final MappingManager mappingManager;
    private Mapper<FailedEvent> failedEventMapper;

    public FailedEventRepository(MappingManager mappingManager) {
        this.mappingManager = mappingManager;
        this.failedEventMapper = mappingManager.mapper(FailedEvent.class);
    }

    public void save(String failureType, UUID eventId, String errorMessage) {
        FailedEvent existingFailedEvent = failedEventMapper.get(failureType, eventId);
        if (null == existingFailedEvent) {
            FailedEvent failedEvent = new FailedEvent(failureType, eventId, errorMessage);
            failedEventMapper.save(failedEvent);
        } else {
            int retries = existingFailedEvent.getRetries();
            FailedEvent failedEvent = new FailedEvent(failureType, eventId, errorMessage, ++retries);
            failedEventMapper.save(failedEvent);
        }
    }

    public List<FailedEvent> getFailedEvents(String failureType, int limit) {
        Select select = select().from(CF_FAILED_EVENTS).where(eq(FAILURE_TYPE, failureType)).limit(limit);
        ResultSet resultSet = mappingManager.getSession().execute(select);
        return failedEventMapper.map(resultSet).all();
    }

    public void deleteFailedEvent(String failureType, UUID eventId) {
        failedEventMapper.delete(failureType, eventId);
    }
}
