package deti.fitmonitor.gyms.IT;

import deti.fitmonitor.gyms.controllers.GetTokenController;
import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.services.JwtUtilService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

public class GymIT {
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


    @Value("${JWT_TOKEN_IT_TESTS}")
    private String token;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    @Disabled
    void testCreateGym_ReturnGym() {
        Gym gym = new Gym();
        gym.setGymName("Gym 1");
        gym.setCapacity(100);
        gym.setOccupancy(0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer " + jwtToken);
        headers.setBearerAuth(token);

        System.out.println(headers);

        HttpEntity<Gym> request = new HttpEntity<>(gym, headers);

        ResponseEntity<Gym> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/gyms/create",
                HttpMethod.POST,
                request,
                Gym.class);

        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        Gym createdGym = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Order(2)
    @Disabled
    void testGetGym_ThenReturnGym(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        System.out.println(headers);

        HttpEntity<Gym> request = new HttpEntity<>(headers);

        ResponseEntity<Gym> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/gyms?id=1",
                HttpMethod.GET,
                request,
                Gym.class);

        Gym responseGym = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
