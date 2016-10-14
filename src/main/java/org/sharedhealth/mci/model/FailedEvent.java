package org.sharedhealth.mci.model;


import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.sharedhealth.mci.util.TimeUuidUtil;

import java.util.Date;
import java.util.UUID;

import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_FAILED_EVENTS)
public class FailedEvent {

    @Column(name = FAILURE_TYPE)
    @PartitionKey
    private String failureType;

    @Column(name = EVENT_ID)
    @PartitionKey(value = 1)
    private UUID eventId;

    @Column(name = ERROR_MESSAGE)
    private String errorMessage;

    @Column(name = FAILED_AT)
    private UUID failedAt;

    @Column(name = RETRIES)
    private int retries;

    public FailedEvent() {
    }

    public FailedEvent(String failureType, UUID eventId, String errorMessage) {
        this(failureType, eventId, errorMessage, 0);
    }

    public FailedEvent(String failureType, UUID eventId, String errorMessage, int retries) {
        this.failureType = failureType;
        this.eventId = eventId;
        this.errorMessage = errorMessage;
        this.retries = retries;
        this.failedAt = TimeUuidUtil.uuidForDate(new Date());
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setFailedAt(UUID failedAt) {
        this.failedAt = failedAt;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public String getFailureType() {
        return failureType;
    }

    public UUID getEventId() {
        return eventId;
    }

    public int getRetries() {
        return retries;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public UUID getFailedAt() {
        return failedAt;
    }
}
