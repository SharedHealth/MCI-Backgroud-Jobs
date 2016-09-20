package org.sharedhealth.mci.service;


import org.sharedhealth.mci.event.DuplicatePatientEventProcessor;
import org.sharedhealth.mci.event.DuplicatePatientEventProcessorFactory;
import org.sharedhealth.mci.model.PatientUpdateLog;
import org.sharedhealth.mci.repository.MarkerRepository;
import org.sharedhealth.mci.repository.PatientFeedRepository;

import java.io.IOException;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sharedhealth.mci.util.Constants.DUPLICATE_PATIENT_MARKER;

public class DuplicatePatientFeedService {

    private PatientFeedRepository feedRepository;
    private MarkerRepository markerRepository;
    private DuplicatePatientEventProcessorFactory eventProcessorFactory;

    public DuplicatePatientFeedService(PatientFeedRepository feedRepository, MarkerRepository markerRepository,
                                       DuplicatePatientEventProcessorFactory eventProcessorFactory) {
        this.feedRepository = feedRepository;
        this.markerRepository = markerRepository;
        this.eventProcessorFactory = eventProcessorFactory;
    }

    public void processDuplicatePatients() throws IOException {
        String markerString = markerRepository.find(DUPLICATE_PATIENT_MARKER);
        UUID marker = isNotBlank(markerString) ? fromString(markerString) : null;
        PatientUpdateLog log = feedRepository.findPatientUpdateLog(marker);
        if (log == null) return;

        DuplicatePatientEventProcessor eventProcessor = eventProcessorFactory
                .getEventProcessor(log.getEventType(), log.getChangeSet());

        eventProcessor.process(log, log.getEventId());
    }
}
