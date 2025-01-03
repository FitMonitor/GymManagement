package deti.fitmonitor.gyms.services;

import static org.mockito.Mockito.*;

import deti.fitmonitor.gyms.models.Machine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
@Slf4j
class KafkaServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private MachineService machineService;

    @Mock
    private GymService gymService;

    @Mock
    private JwtUtilService jwtUtilService;

    @InjectMocks
    private KafkaService kafkaService;

    @Test
    void testProcessMachineRequest_UseMachine_ShouldSendTrue() {
        String message = "1 use user123";
        String correlationId = "correlationId123";
        Machine mockMachine = new Machine();
        mockMachine.setUserSub("123");
        mockMachine.setAvailable(true);

        when(machineService.useMachine(1L, "use", "user123")).thenReturn(true);
        when(gymService.isUserInGym(1L, "user123")).thenReturn(true);

        kafkaService.processMachineRequest(message, correlationId);

        verify(kafkaTemplate, times(1)).send("reply-topic", correlationId, "True");
    }

    @Test
    void testProcessMachineRequest_UserAlreadyUsingMachine_ShouldSendUserAlreadyUsingAMachine() {
        String message = "1 use user123";
        String correlationId = "correlationId123";

        when(machineService.getMachineByUserSub("user123")).thenReturn(new Machine());
        when(gymService.isUserInGym(1L, "user123")).thenReturn(true);

        kafkaService.processMachineRequest(message, correlationId);

        verify(kafkaTemplate, times(1)).send("reply-topic", correlationId, "User already using a machine");
    }

    @Test
    void testProcessMachineRequest_MachineAlreadyInUse_ShouldSendFalse() {
        String message = "1 use user123";
        String correlationId = "correlationId123";
        Machine mockMachine = new Machine();
        mockMachine.setUserSub("user456");
        mockMachine.setAvailable(false);

        when(machineService.useMachine(1L, "use", "user123")).thenReturn(false);
        when(gymService.isUserInGym(1L, "user123")).thenReturn(true);

        kafkaService.processMachineRequest(message, correlationId);

        verify(kafkaTemplate, times(1)).send("reply-topic", correlationId, "False");
    }

    @Test
    void testProcessMachineRequest_UserNotInGym_ShouldSendUserNotInGym() {
        String message = "1 use user123";
        String correlationId = "correlationId123";

        when(gymService.isUserInGym(1L, "user123")).thenReturn(false);

        kafkaService.processMachineRequest(message, correlationId);

        verify(kafkaTemplate, times(1)).send("reply-topic", correlationId, "User not in gym");
    }

    @Test
    void testProcessUserGymEntrance_ValidToken_UserEntersGym() throws Exception {
        String message = "validToken";
        String correlationId = "correlationId123";
        String userSub = "user123";

        when(jwtUtilService.validateToken("validToken")).thenReturn(true);
        when(jwtUtilService.extractUserSub("validToken")).thenReturn(userSub);

        when(gymService.isUserInGym(1L, userSub)).thenReturn(false);
        when(gymService.isGymFull(1L)).thenReturn(false);

        kafkaService.processUserGymEntrance(message, correlationId);

        verify(kafkaTemplate, times(1)).send("reply-topic", correlationId, "Entered");
    }

    @Test
    void testProcessUserGymEntrance_GymFull_ShouldSendFull() throws Exception {
        String message = "validToken";
        String correlationId = "correlationId123";
        String userSub = "user123";

        when(jwtUtilService.validateToken("validToken")).thenReturn(true);
        when(jwtUtilService.extractUserSub("validToken")).thenReturn(userSub);

        when(gymService.isUserInGym(1L, userSub)).thenReturn(false);
        when(gymService.isGymFull(1L)).thenReturn(true);

        kafkaService.processUserGymEntrance(message, correlationId);

        verify(kafkaTemplate, times(1)).send("reply-topic", correlationId, "Full");
    }

    @Test
    void testProcessUserGymEntrance_InvalidToken_ShouldNotProcess() throws Exception {
        String message = "invalidToken";
        String correlationId = "correlationId123";

        when(jwtUtilService.validateToken("invalidToken")).thenReturn(false);

        kafkaService.processUserGymEntrance(message, correlationId);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }
}
