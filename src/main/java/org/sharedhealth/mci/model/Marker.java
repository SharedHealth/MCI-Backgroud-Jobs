package org.sharedhealth.mci.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

import static org.sharedhealth.mci.util.Constants.*;

@Table(name = CF_MARKER)
public class Marker {

    @Column(name = TYPE)
    @PartitionKey
    private String type;

    @Column(name = CREATED_AT)
    @PartitionKey(value = 1)
    private UUID createdAt;

    @Column(name = MARKER)
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(UUID createdAt) {
        this.createdAt = createdAt;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String markerValue) {
        this.value = markerValue;
    }
}
