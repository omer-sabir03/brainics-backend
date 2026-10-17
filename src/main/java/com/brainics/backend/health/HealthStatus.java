package com.brainics.backend.health;

import java.time.Instant;

public record HealthStatus(
        String status,
        String application,
        String profile,
        String database,
        String schemaVersion,
        Instant time) {
}
