package org.sharedhealth.mci.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sharedhealth.mci.config.MCIProperties;
import org.sharedhealth.mci.model.FailedEvent;
import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.repository.FailedEventRepository;
import org.sharedhealth.mci.repository.MarkerRepository;
import org.sharedhealth.mci.repository.PatientFeedRepository;
import org.sharedhealth.mci.service.HealthIdMarkUsedService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sharedhealth.mci.util.Constants.*;

public class HealthIdMarkUsedTask {
    private static final Logger logger = LogManager.getLogger(HealthIdMarkUsedTask.class);

    private MCIProperties mciProperties;
    private MarkerRepository markerRepository;
    private PatientFeedRepository feedRepository;
    private FailedEventRepository failedEventRepository;
    private HealthIdMarkUsedService healthIdMarkUsedService;

    public HealthIdMarkUsedTask(HealthIdMarkUsedService healthIdMarkUsedService, PatientFeedRepository feedRepository,
                                MarkerRepository markerRepository, FailedEventRepository failedEventRepository, MCIProperties mciProperties) {
        this.healthIdMarkUsedService = healthIdMarkUsedService;
        this.feedRepository = feedRepository;
        this.markerRepository = markerRepository;
        this.failedEventRepository = failedEventRepository;
        this.mciProperties = mciProperties;
    }

    public void process() {
        List<FailedEvent> failedEvents = failedEventRepository.getFailedEvents(FAILURE_TYPE_HEALTH_MARK_USED, mciProperties.getMaxFailedEventLimit());
        if (failedEvents.size() >= mciProperties.getMaxFailedEventLimit()) {
            logger.info("Not processing HealthId mark used task because failed events have reached to max limit");
            return;
        }

        String markerValue = markerRepository.find(HEALTH_ID_MARK_USED_MARKER);
        UUID marker = isNotBlank(markerValue) ? fromString(markerValue) : null;

        List<PatientUpdateLog> updateLogs = feedRepository.findPatientUpdateLog(marker, mciProperties.getHidMarkUsedBlockSize());
        if (updateLogs == null || updateLogs.size() == 0) return;
        for (PatientUpdateLog updateLog : updateLogs) {
            if (!EVENT_TYPE_CREATED.equals(updateLog.getEventType())) continue;
            String healthId = updateLog.getHealthId();
            UUID eventId = updateLog.getEventId();
            try {
                logger.info("Marking HealthId {} as used", healthId);
                healthIdMarkUsedService.markUsed(healthId, eventId);
            } catch (IOException e) {
                logger.error("Failed to mark HealthId {} as Used", healthId, e);
                failedEventRepository.save(FAILURE_TYPE_HEALTH_MARK_USED, eventId, e.getMessage());
            }
        }
        PatientUpdateLog lastProcessedUpdateLog = updateLogs.get(updateLogs.size() - 1);
        markerRepository.save(HEALTH_ID_MARK_USED_MARKER, lastProcessedUpdateLog.getEventId().toString());
    }

    public void processFailedEvents() {
        List<FailedEvent> failedEvents = failedEventRepository.getFailedEvents(FAILURE_TYPE_HEALTH_MARK_USED,
                mciProperties.getFailedEventProcessBlockSize());
        for (FailedEvent failedEvent : failedEvents) {
            if (failedEvent.getRetries() >= mciProperties.getFailedEventRetryLimit()) {
                logger.warn("Cannot process failed event with event-id {} because it has reached the retry limit", failedEvent.getEventId());
                continue;
            }
            PatientUpdateLog patientUpdateLog = feedRepository.findPatientUpdateLogByEventId(failedEvent.getEventId());
            UUID eventId = patientUpdateLog.getEventId();
            String healthId = patientUpdateLog.getHealthId();
            try {
                logger.info("Marking HealthId {} as used from failed events", healthId);
                healthIdMarkUsedService.markUsed(healthId, eventId);
                failedEventRepository.deleteFailedEvent(FAILURE_TYPE_HEALTH_MARK_USED, failedEvent.getEventId());
            } catch (Exception e) {
                logger.error("Failed to mark HealthId {} as Used from failed events", healthId, e);
                failedEventRepository.save(FAILURE_TYPE_HEALTH_MARK_USED, failedEvent.getEventId(), e.toString());
            }
        }
    }
}
