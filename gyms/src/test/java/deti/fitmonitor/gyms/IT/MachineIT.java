package deti.fitmonitor.gyms.IT;

import deti.fitmonitor.gyms.controllers.GetTokenController;
import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.services.JwtUtilService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.profiles.active=test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MachineIT {

    @MockBean
    private JwtUtilService jwtUtilService;

    @MockBean
    private GetTokenController getTokenController;

    @Container
    public static GenericContainer container = new GenericContainer("mysql:latest")
        .withExposedPorts(3306)
        .withEnv("MYSQL_ROOT_PASSWORD", "rootpass")
        .withEnv("MYSQL_DATABASE", "mydatabase")
        .withEnv("MYSQL_USER", "user")
        .withEnv("MYSQL_PASSWORD", "secret");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = "jdbc:mysql://" + container.getHost() + ":" + container.getMappedPort(3306) + "/mydatabase";
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> "user");
        registry.add("spring.datasource.password", () -> "secret");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    //do beforeAll to get token after

    @Test
    @Order(1)
    void whenGetAllMachinesReturnAllMachines() {
        ResponseEntity<List<Machine>> response = restTemplate.exchange(
                "http://localhost:" + port + "/machine/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Machine>>() {
                });

        List<Machine> machines = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(4, machines.size());
    }

    @Test
    @Order(2)
    void whenCreateMachineReturnMachine(){
        Machine machine = new Machine();
        machine.setName("Machine 5");
        machine.setAvailable(true);
        machine.setDescription("Description 5");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Machine> request = new HttpEntity<>(machine, headers);

        ResponseEntity<Machine> response = restTemplate.exchange(
                "http://localhost:" + port + "/machine",
                HttpMethod.POST,
                request,
                Machine.class);

        Machine createdMachine = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertEquals("Machine 5", createdMachine.getName());
        assertEquals("Description 5", createdMachine.getDescription());
    }

}