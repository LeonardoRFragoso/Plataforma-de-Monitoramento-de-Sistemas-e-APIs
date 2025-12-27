package com.apm.platform.infrastructure.persistence;

import com.apm.platform.domain.entity.MonitoredSystem;
import com.apm.platform.domain.port.outgoing.MonitoredSystemRepository;
import com.apm.platform.domain.valueobject.SystemStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.apm.platform.infrastructure.persistence.adapter")
class MonitoredSystemRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("apm_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MonitoredSystemRepository repository;

    @Test
    void shouldSaveAndFindMonitoredSystem() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API",
            "https://api.test.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );

        MonitoredSystem saved = repository.save(system);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test API");
        assertThat(saved.getBaseUrl()).isEqualTo("https://api.test.com");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCurrentStatus()).isEqualTo(SystemStatus.UP);
    }

    @Test
    void shouldFindSystemById() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API",
            "https://api.test.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );

        MonitoredSystem saved = repository.save(system);

        Optional<MonitoredSystem> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getName()).isEqualTo("Test API");
    }

    @Test
    void shouldFindSystemByName() {
        MonitoredSystem system = MonitoredSystem.create(
            "Unique API Name",
            "https://api.test.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );

        repository.save(system);

        Optional<MonitoredSystem> found = repository.findByName("Unique API Name");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Unique API Name");
    }

    @Test
    void shouldCheckIfSystemExistsByName() {
        MonitoredSystem system = MonitoredSystem.create(
            "Existing API",
            "https://api.test.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );

        repository.save(system);

        assertThat(repository.existsByName("Existing API")).isTrue();
        assertThat(repository.existsByName("Non Existing API")).isFalse();
    }

    @Test
    void shouldFindAllActiveSystems() {
        MonitoredSystem activeSystem = MonitoredSystem.create(
            "Active API",
            "https://api.test.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );

        MonitoredSystem inactiveSystem = MonitoredSystem.create(
            "Inactive API",
            "https://api2.test.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );
        inactiveSystem.deactivate();

        repository.save(activeSystem);
        repository.save(inactiveSystem);

        List<MonitoredSystem> activeSystems = repository.findAllActive();

        assertThat(activeSystems).hasSize(1);
        assertThat(activeSystems.get(0).getName()).isEqualTo("Active API");
        assertThat(activeSystems.get(0).isActive()).isTrue();
    }

    @Test
    void shouldFindSystemsByEnvironment() {
        MonitoredSystem prodSystem = MonitoredSystem.create(
            "Prod API",
            "https://api.prod.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );

        MonitoredSystem devSystem = MonitoredSystem.create(
            "Dev API",
            "https://api.dev.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.DEVELOPMENT,
            60
        );

        repository.save(prodSystem);
        repository.save(devSystem);

        List<MonitoredSystem> prodSystems = repository.findByEnvironment(MonitoredSystem.Environment.PRODUCTION);

        assertThat(prodSystems).hasSize(1);
        assertThat(prodSystems.get(0).getName()).isEqualTo("Prod API");
        assertThat(prodSystems.get(0).getEnvironment()).isEqualTo(MonitoredSystem.Environment.PRODUCTION);
    }

    @Test
    void shouldUpdateSystemStatus() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API",
            "https://api.test.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );

        MonitoredSystem saved = repository.save(system);
        
        saved.updateStatus(SystemStatus.DEGRADED);
        MonitoredSystem updated = repository.save(saved);

        Optional<MonitoredSystem> found = repository.findById(updated.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCurrentStatus()).isEqualTo(SystemStatus.DEGRADED);
        assertThat(found.get().getLastCheckAt()).isNotNull();
    }

    @Test
    void shouldDeleteSystemById() {
        MonitoredSystem system = MonitoredSystem.create(
            "Test API",
            "https://api.test.com",
            MonitoredSystem.SystemType.API,
            MonitoredSystem.Environment.PRODUCTION,
            60
        );

        MonitoredSystem saved = repository.save(system);
        String systemId = saved.getId();

        repository.deleteById(systemId);

        Optional<MonitoredSystem> found = repository.findById(systemId);
        assertThat(found).isEmpty();
    }
}
