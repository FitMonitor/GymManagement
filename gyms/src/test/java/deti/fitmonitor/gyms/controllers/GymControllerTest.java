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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    
        Gym gym = new Gym();
        gym.setGymName("Test Gym");
        gym.setCapacity(100);
        gym.setOccupancy(0);

        when(gymService.createGym("Test Gym", 100)).thenReturn(gym);

        mockMvc.perform(post("/api/gyms/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(gym)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gymName").value("Test Gym"))
                .andExpect(jsonPath("$.capacity").value(100));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void createGym_MissingFields_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/gyms/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void getGym_ValidRequest_ShouldReturnGym() throws Exception {
        Gym gym = new Gym();
        gym.setGymName("test");
        when(gymService.getGymByID(1L)).thenReturn(gym);

        mockMvc.perform(get("/api/gyms?id=1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gymName").value("test"));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void getGymOccupancy_ValidRequest_ShouldReturnGym() throws Exception {
        // Arrange
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setOccupancy(20);
        gym.setCapacity(50);

        // Mock the service call
        when(gymService.getOccupancy(1L)).thenReturn(20);

        // Act & Assert
        mockMvc.perform(get("/api/gyms/occupancy?id=1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }


    @WithMockUser(username = "user", roles = "USER")
    @Test
    void getGymOccupancy_MissingId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(get("/api/gyms/occupancy?id=1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
    }
}
