package org.sharedhealth.mci.repository;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.junit.Before;
import org.junit.Test;
import org.sharedhealth.mci.BaseIntegrationTest;
import org.sharedhealth.mci.config.MCICassandraConfig;
import org.sharedhealth.mci.model.Marker;

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