package org.sharedhealth.mci.launch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sharedhealth.mci.config.MCIProperties;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    private static void createDedupTaskScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }


    public static void main(String[] args) {
        logger.info("Starting MCI background jobs");
        createDedupTaskScheduler();
    }
}