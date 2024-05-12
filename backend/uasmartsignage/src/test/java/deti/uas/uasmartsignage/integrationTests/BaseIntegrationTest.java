package deti.uas.uasmartsignage.integrationTests;

import org.junit.jupiter.api.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {"spring.profiles.active=test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class BaseIntegrationTest {

    public static PostgreSQLContainer container;

    public static PostgreSQLMosquittoContainer container1 = new PostgreSQLMosquittoContainer();

    public static int getPostgresPort() {
        return container1.getPostgresPort();
    }

    public static int getMosquittoPort() {
        return container1.getMosquittoPort();
    }

    static {
        container = (PostgreSQLContainer) new PostgreSQLContainer<>("postgres:latest").withReuse(true)
                .withDatabaseName("uasmartsignageIT")
                .withUsername("integrationTest")
                .withPassword("test");
        container.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }
}
