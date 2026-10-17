package com.brainics.backend.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;

@Service
public class HealthService {

    private static final Logger log = LoggerFactory.getLogger(HealthService.class);

    private final JdbcTemplate jdbc;
    private final Environment environment;
    private final String applicationName;

    public HealthService(JdbcTemplate jdbc,
                         Environment environment,
                         @Value("${spring.application.name}") String applicationName) {
        this.jdbc = jdbc;
        this.environment = environment;
        this.applicationName = applicationName;
    }

    /** Checks the app and the Neon database connection. */
    public HealthStatus check() {
        boolean dbUp = isDatabaseUp();
        String profile = String.join(",", Arrays.asList(environment.getActiveProfiles()));
        return new HealthStatus(
                dbUp ? "UP" : "DOWN",
                applicationName,
                profile,
                dbUp ? "UP" : "DOWN",
                dbUp ? latestMigration() : "unknown",
                Instant.now());
    }

    private boolean isDatabaseUp() {
        try {
            Integer one = jdbc.queryForObject("SELECT 1", Integer.class);
            return one != null && one == 1;
        } catch (DataAccessException ex) {
            log.warn("Database health check failed: {}", ex.getMessage());
            return false;
        }
    }

    /** Latest Flyway migration applied, e.g. "1". Proves migrations ran on Neon. */
    private String latestMigration() {
        try {
            String version = jdbc.queryForObject(
                    "SELECT version FROM flyway_schema_history "
                            + "WHERE success = true AND version IS NOT NULL "
                            + "ORDER BY installed_rank DESC LIMIT 1",
                    String.class);
            return version == null ? "unknown" : version;
        } catch (DataAccessException ex) {
            return "unknown";
        }
    }
}
