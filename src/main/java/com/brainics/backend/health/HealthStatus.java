package com.brainics.backend.health;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatus {
    private String status;
    private String application;
    private String profile;
    private String database;
    private String schemaVersion;
    private Instant time;
}
