package com.example.aiticketassistant.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FlywayMigrationTest {
    @Test
    void migrationFilesExist() {
        assertThat(Files.exists(Path.of("src/main/resources/db/migration/V1__init_core_schema.sql"))).isTrue();
        assertThat(Files.exists(Path.of("src/main/resources/db/migration/V2__seed_demo_data.sql"))).isTrue();
    }
}
