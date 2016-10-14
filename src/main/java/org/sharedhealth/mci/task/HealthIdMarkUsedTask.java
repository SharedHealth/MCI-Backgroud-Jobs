package org.sharedhealth.mci.task;

import org.sharedhealth.mci.config.MCIProperties;
import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.repository.MarkerRepository;
import org.sharedhealth.mci.repository.PatientFeedRepository;
import org.sharedhealth.mci.service.HealthIdMarkUsedService;

import java.util.List;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sharedhealth.mci.util.Constants.EVENT_TYPE_CREATED;
import static org.sharedhealth.mci.util.Constants.HEALTH_ID_MARK_USED_MARKER;

public class HealthIdMarkUsedTask {
    private HealthIdMarkUsedService healthIdMarkUsedService;
    private PatientFeedRepository feedRepository;
    private MarkerRepository markerRepository;
    private MCIProperties mciProperties;

    public HealthIdMarkUsedTask(HealthIdMarkUsedService healthIdMarkUsedService, PatientFeedRepository feedRepository,
                                MarkerRepository markerRepository, MCIProperties mciProperties) {
        this.healthIdMarkUsedService = healthIdMarkUsedService;
        this.feedRepository = feedRepository;
        this.markerRepository = markerRepository;
        this.mciProperties = mciProperties;
    }

    public void markUsedHealthIds() {
        String markerValue = markerRepository.find(HEALTH_ID_MARK_USED_MARKER);
        UUID marker = isNotBlank(markerValue) ? fromString(markerValue) : null;

        List<PatientUpdateLog> updateLogs = feedRepository.findPatientUpdateLog(marker, mciProperties.getHidMarkUsedBlockSize());
        if (updateLogs == null || updateLogs.size() == 0) return;
        for (PatientUpdateLog updateLog : updateLogs) {
            if (!EVENT_TYPE_CREATED.equals(updateLog.getEventType())) continue;
            healthIdMarkUsedService.markUsed(updateLog.getHealthId(), updateLog.getEventId());
        }
        PatientUpdateLog lastProcessedUpdateLog = updateLogs.get(updateLogs.size() - 1);

        markerRepository.save(HEALTH_ID_MARK_USED_MARKER, lastProcessedUpdateLog.getEventId().toString());
    }
}
