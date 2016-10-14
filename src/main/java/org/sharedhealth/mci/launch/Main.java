package org.sharedhealth.mci.launch;

import com.datastax.driver.mapping.MappingManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sharedhealth.mci.WebClient;
import org.sharedhealth.mci.config.MCICassandraConfig;
import org.sharedhealth.mci.config.MCIProperties;
import org.sharedhealth.mci.model.IdentityStore;
import org.sharedhealth.mci.repository.FailedEventRepository;
import org.sharedhealth.mci.repository.MarkerRepository;
import org.sharedhealth.mci.repository.PatientFeedRepository;
import org.sharedhealth.mci.service.HealthIdMarkUsedService;
import org.sharedhealth.mci.service.IdentityProviderService;
import org.sharedhealth.mci.task.HealthIdMarkUsedTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    private static MCIProperties mciProperties;
    private static HealthIdMarkUsedTask healthIdMarkUsedTask;

    private static void createHealthIdMarkUsedTaskScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
                    healthIdMarkUsedTask.process();
                    healthIdMarkUsedTask.processFailedEvents();
                },
                mciProperties.getHidMarkUsedTaskInitialDelay(), mciProperties.getHidMarkUsedTaskDelay(),
                TimeUnit.MILLISECONDS);
    }


    public static void main(String[] args) {
        logger.info("Starting MCI background jobs");

        mciProperties = MCIProperties.getInstance();
        IdentityStore identityStore = new IdentityStore();
        WebClient webClient = new WebClient(identityStore);
        MappingManager mappingManager = MCICassandraConfig.getInstance().getMappingManager();

        IdentityProviderService identityProviderService = new IdentityProviderService(webClient, identityStore);
        HealthIdMarkUsedService markUsedService = new HealthIdMarkUsedService(identityProviderService, webClient, mciProperties);

        MarkerRepository markerRepository = new MarkerRepository(mappingManager);
        PatientFeedRepository feedRepository = new PatientFeedRepository(mappingManager);
        FailedEventRepository failedEventRepository = new FailedEventRepository(mappingManager);

        healthIdMarkUsedTask = new HealthIdMarkUsedTask(markUsedService, feedRepository, markerRepository, failedEventRepository, mciProperties);
        createHealthIdMarkUsedTaskScheduler();
    }

}