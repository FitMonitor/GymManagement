package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.services.GymService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import deti.fitmonitor.gyms.services.JwtUtilService;

import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GymController.class)
@AutoConfigureMockMvc(addFilters = false)
class GymControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GymService gymService;

    @MockBean
    private JwtUtilService jwtUtilService;

    
    @Test
    void getGym_ValidRequest_ShouldReturnGym() throws Exception {
        Gym gym = new Gym();
        gym.setGymName("test");
        when(gymService.getGymByID(1L)).thenReturn(gym);

        mockMvc.perform(get("/default/api/gyms?id=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gymName").value("test"));
    }

    @Test
    void getGym_InvalidRequest_GymNotFound() throws Exception {
        mockMvc.perform(get("/default/api/gyms?id=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void getGymOccupancy_ValidRequest_ShouldReturnGym() throws Exception {
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setOccupancy(20);
        gym.setCapacity(50);

        when(gymService.getOccupancy(1L)).thenReturn(20);

        mockMvc.perform(get("/default/api/gyms/occupancy?id=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void getGymOccupancy_MissingId_ShouldReturnNoContent() throws Exception {
        when(gymService.getOccupancy(500L)).thenReturn(null);

        mockMvc.perform(get("/default/api/gyms/occupancy?id=500")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
