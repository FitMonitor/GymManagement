package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.services.GymService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GymController.class)
class GymControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymService gymService;

    @BeforeEach
    void setUp() {
        gymService = Mockito.mock(GymService.class);
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void createGym_ValidRequest_ShouldReturnCreated() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("gymName", "Test Gym");
        requestBody.put("capacity", "100");

        Gym gym = new Gym();
        gym.setGymName("Test Gym");
        gym.setCapacity(100);
        gym.setOccupancy(0);

        when(gymService.createGym("Test Gym", 100)).thenReturn(gym);

        mockMvc.perform(post("/api/gyms/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gymName\": \"Test Gym\", \"capacity\": \"100\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gymName").value("Test Gym"));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void createGym_MissingFields_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/api/gyms/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void getGym_ValidRequest_ShouldReturnGym() throws Exception {
        Gym gym = new Gym();
        gym.setGymName("Test Gym");
        when(gymService.getGym("Test Gym")).thenReturn(gym);

        mockMvc.perform(post("/api/gyms/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gymName\": \"Test Gym\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gymName").value("Test Gym"));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void getGym_MissingName_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/api/gyms/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void getGymOccupancy_ValidRequest_ShouldReturnGym() throws Exception {
        // Arrange
        Gym gym = new Gym();
        gym.setOccupancy(20);
        gym.setCapacity(50);

        // Mock the service call
        when(gymService.getOccupancy(1L)).thenReturn(gym);

        // Act & Assert
        mockMvc.perform(post("/api/gyms/occupancy")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gymId\": \"1\"}")).andExpect(jsonPath("$.occupancy").value(20)).andExpect(status().isOk()); // Expecting occupancy to be 50
    }


    @WithMockUser(username = "user", roles = "USER")
    @Test
    void getGymOccupancy_MissingId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/api/gyms/occupancy")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNoContent());
    }
}
