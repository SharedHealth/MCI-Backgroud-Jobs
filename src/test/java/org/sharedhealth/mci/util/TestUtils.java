package org.sharedhealth.mci.util;

import com.datastax.driver.core.Session;
import org.sharedhealth.mci.config.MCICassandraConfig;

import java.util.List;

import static java.util.Arrays.asList;

public class TestUtils {
    public static void truncateAllColumnFamilies() {
        Session session = MCICassandraConfig.getInstance().getMappingManager().getSession();
        List<String> cfs = getAllColumnFamilies();
        for (String cf : cfs) {
            session.execute("truncate " + cf);
        }
    }

    private static List<String> getAllColumnFamilies() {
        return asList(
                Constants.CF_MARKER,
                Constants.CF_PATIENT_UPDATE_LOG
        );
    }

}
