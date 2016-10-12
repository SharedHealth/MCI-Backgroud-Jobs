package org.sharedhealth.mci.repository;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sharedhealth.mci.BaseIntegrationTest;
import org.sharedhealth.mci.config.MCICassandraConfig;
import org.sharedhealth.mci.model.Marker;
import org.sharedhealth.mci.util.TestUtils;

import static com.datastax.driver.core.utils.UUIDs.timeBased;
import static org.junit.Assert.assertEquals;

public class MarkerRepositoryIT extends BaseIntegrationTest {
    private MarkerRepository markerRepository;
    private Mapper<Marker> markerMapper;

    @Before
    public void setUp() throws Exception {
        MappingManager mappingManager = MCICassandraConfig.getInstance().getMappingManager();
        markerRepository = new MarkerRepository(mappingManager);
        markerMapper = mappingManager.mapper(Marker.class);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.truncateAllColumnFamilies();
    }

    @Test
    public void shouldRetrieveAnExistingMarkerByType() throws Exception {
        Marker marker = new Marker();
        String permanent = "permanent";
        marker.setType(permanent);
        String value = "black";
        marker.setValue(value);
        marker.setCreatedAt(timeBased());
        markerMapper.save(marker);

        String markerValue = markerRepository.find(permanent);
        assertEquals(value, markerValue);
    }
}
