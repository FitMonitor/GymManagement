package deti.fitmonitor.gyms.services;

import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.repositories.MachineRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MachineServiceTest {

    @Mock
    private MachineRepository machineRepository;

    @InjectMocks
    private MachineService machineService;

    @Test
    void testCreateMachine() {
        Machine machine = new Machine();
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setDescription("Description 1");

        when(machineRepository.save(any(Machine.class))).thenReturn(machine);

        Machine createdMachine = machineService.createMachine(machine);

        assertEquals(machine.getName(), createdMachine.getName());
        assertEquals(machine.isAvailable(), createdMachine.isAvailable());
        assertEquals(machine.getDescription(), createdMachine.getDescription());
    }

    @Test
    void testGetMachine() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setDescription("Description 1");

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));

        Machine foundMachine = machineService.getMachine(1L);

        assertEquals(machine.getId(), foundMachine.getId());
        assertEquals(machine.getName(), foundMachine.getName());
        assertEquals(machine.isAvailable(), foundMachine.isAvailable());
        assertEquals(machine.getDescription(), foundMachine.getDescription());
    }

    @Test
    void testGetAllMachines() {
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

        List<Machine> machines = new ArrayList<>(Arrays.asList(machine1, machine2));

        when(machineRepository.findAll()).thenReturn(machines);

        List<Machine> foundMachines = machineService.getAllMachines();

        assertEquals(machines.size(), foundMachines.size());
        assertEquals(machines.get(0).getId(), foundMachines.get(0).getId());
        assertEquals(machines.get(0).getName(), foundMachines.get(0).getName());
        assertEquals(machines.get(0).isAvailable(), foundMachines.get(0).isAvailable());
        assertEquals(machines.get(0).getDescription(), foundMachines.get(0).getDescription());
        assertEquals(machines.get(1).getId(), foundMachines.get(1).getId());
        assertEquals(machines.get(1).getName(), foundMachines.get(1).getName());
        assertEquals(machines.get(1).isAvailable(), foundMachines.get(1).isAvailable());
        assertEquals(machines.get(1).getDescription(), foundMachines.get(1).getDescription());
    
    }

    @Test
    void tesGetMachineByUserSub(){
        Machine machine1 = new Machine();
        machine1.setId(1L);
        machine1.setName("Machine 1");
        machine1.setAvailable(true);
        machine1.setDescription("Description 1");
        machine1.setUserSub("feu1842572489ufjoekwj");

        Machine machine2 = new Machine();
        machine2.setId(2L);
        machine2.setName("Machine 2");
        machine2.setAvailable(false);
        machine2.setDescription("Description 2");
        machine2.setUserSub("feu1842572489ufjfrgirigij1324");

        when(machineRepository.findByUserSub("feu1842572489ufjfrgirigij1324")).thenReturn(Optional.of(machine2));

        Machine foundMachine = machineService.getMachineByUserSub("feu1842572489ufjfrgirigij1324");

        assertNotNull(foundMachine);
        assertEquals(2L, foundMachine.getId());
        assertEquals("Machine 2", foundMachine.getName());
        assertFalse(foundMachine.isAvailable());
        assertEquals("Description 2", foundMachine.getDescription());
        assertEquals("feu1842572489ufjfrgirigij1324", foundMachine.getUserSub());
    }

    @Test
    void testUseMachineSuccess() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setUserSub(null);

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(machineRepository.save(any())).thenReturn(machine);

        Boolean result = machineService.useMachine(1L, "use", "user123");

        assertTrue(result);
        assertFalse(machine.isAvailable());
        assertEquals("user123", machine.getUserSub());
        assertEquals("user123", machine.getUserSub());
        verify(machineRepository, times(1)).save(machine);
    }

    @Test
    void testUseMachineAlreadyInUse() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Machine 1");
        machine.setAvailable(false);
        machine.setUserSub("user456");

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));

        Boolean result = machineService.useMachine(1L, "use", "user123");

        assertFalse(result);
        verify(machineRepository, never()).save(any());
    }

    @Test
    void testLeaveMachineSuccess() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Machine 1");
        machine.setAvailable(false);
        machine.setUserSub("user123");

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(machineRepository.save(any())).thenReturn(machine);

        Boolean result = machineService.useMachine(1L, "leave", "user123");

        assertTrue(result);
        assertTrue(machine.isAvailable());
        assertNull(machine.getUserSub());
        verify(machineRepository, times(1)).save(machine);
    }

    @Test
    void testLeaveMachineNotOwned() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Machine 1");
        machine.setAvailable(false);
        machine.setUserSub("user456");

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));

        Boolean result = machineService.useMachine(1L, "leave", "user123");

        assertFalse(result);
        verify(machineRepository, never()).save(any());
    }

    @Test
    void testUseMachineNotFound() {
        when(machineRepository.findById(1L)).thenReturn(Optional.empty());

        Boolean result = machineService.useMachine(1L, "use", "user123");

        assertFalse(result);
        verify(machineRepository, never()).save(any());
    }

    @Test
    void testInvalidIntention() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setUserSub(null);

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));

        Boolean result = machineService.useMachine(1L, "invalid", "user123");

        assertFalse(result);
        verify(machineRepository, never()).save(any());
    }

    @Test
    void testDeleteMachine() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setDescription("Description 1");

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        doNothing().when(machineRepository).deleteById(1L); // Adjusted to match the service method

        Boolean result = machineService.deleteMachine(1L);

        assertTrue(result);
        verify(machineRepository, times(1)).deleteById(1L); // Verify deleteById is called
    }


    @Test
    void testDeleteMachineNotFound() {
        when(machineRepository.findById(1L)).thenReturn(Optional.empty());

        Boolean result = machineService.deleteMachine(1L);

        assertFalse(result);
        verify(machineRepository, never()).deleteById(any()); // Verifying no deleteById call
    }



    
}
