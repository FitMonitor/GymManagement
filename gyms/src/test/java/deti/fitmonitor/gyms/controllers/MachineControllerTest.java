package deti.fitmonitor.gyms.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.repositories.MachineRepository;
import deti.fitmonitor.gyms.services.JwtUtilService;
import deti.fitmonitor.gyms.services.MachineService;

import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;

@WebMvcTest(MachineController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MachineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MachineService machineService;

    @MockBean
    private JwtUtilService jwtUtilService;


    @Test
    void whenGetMachineByIdReturnMachine() throws Exception {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setDescription("Description 1");

        when(machineService.getMachine(1L)).thenReturn(machine);

        mockMvc.perform(get("/machine?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Machine 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.description", is("Description 1")));
    }

    @Test
    void whenGetAllMachinesReturnAllMachines() throws Exception {
        Machine machine1 = new Machine();
        machine1.setId(1L);
        machine1.setName("Machine 1");
        machine1.setAvailable(true);
        machine1.setDescription("Description 1");

        Machine machine2 = new Machine();
        machine2.setId(2L);
        machine2.setName("Machine 2");
        machine2.setAvailable(false);
        machine2.setDescription("Description 2");

        List<Machine> machines = List.of(machine1, machine2);

        when(machineService.getAllMachines()).thenReturn(machines);

        mockMvc.perform(get("/machine/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Machine 1")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Machine 2")))
                .andExpect(jsonPath("$[1].available", is(false)))
                .andExpect(jsonPath("$[1].description", is("Description 2")));
    }

    @Test
    void whenCreateMachineReturnMachine() throws Exception {
        Machine machine = new Machine();
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setDescription("Description 1");

        when(machineService.createMachine(Mockito.any(Machine.class))).thenReturn(machine);

        mockMvc.perform(post("/machine")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(machine)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Machine 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.description", is("Description 1")));
    }
    
}
