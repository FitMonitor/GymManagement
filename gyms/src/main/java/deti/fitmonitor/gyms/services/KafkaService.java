package deti.fitmonitor.gyms.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final MachineService machineService;
    private final GymService gymService;
    private final JwtUtilService jwtUtilService;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate, MachineService machineService, GymService gymService, JwtUtilService jwtUtilService) {
        this.kafkaTemplate = kafkaTemplate;
        this.machineService = machineService;
        this.gymService = gymService;
        this.jwtUtilService = jwtUtilService;
    }

    @KafkaListener(topics = "machine", groupId = "machine-service-group")
    public void processMachineRequest(
        String message,
        @Header("correlationId") String correlationId
    ) {
        System.out.println("Received message: " + message + " with correlationId: " + correlationId);

        // Parse the message (e.g., machineId, intention, usersub)
        String[] parts = message.split(" ");
        String machineId = parts[0];
        String intention = parts[1];
        String userSub = parts[2];

        // Check machine state (e.g., in use or available)
        String response;
        if (!gymService.isUserInGym(1L, userSub)) {
            response = "User not in gym";
        } else if (machineService.useMachine(Long.parseLong(machineId), intention, userSub)) {
            System.out.println("Machine " + machineId + " is now " + (intention.equals("use") ? "in use" : "available"));
            response = "True";
        } else if (intention.equals("use") && machineService.getMachineByUserSub(userSub) != null) {
            System.out.println("User " + userSub + " is already using a machine");
            response = "User already using a machine";
        } else {
            System.out.println("Machine " + machineId + " is already in use");
            response = "False";
        }

        // Send response to the reply-topic with the same correlationId
        kafkaTemplate.send("reply-topic", correlationId, response);
    }

    @KafkaListener(topics = "user", groupId = "user-service-group")
    public void processUserGymEntrance(
        String message,
        @Header("correlationId") String correlationId
    ) throws Exception {
        System.out.println("Received message: " + message + " with correlationId: " + correlationId);

        // Parse the message (token)
        String token = message;

        //validate token and get userSub
        if(!jwtUtilService.validateToken(token)){
            System.out.println("Invalid token");
            return;
        }
        String userSub = jwtUtilService.extractUserSub(token);

        // Check if the user is already in the gym
        if (gymService.isUserInGym(1L, userSub)) {
            gymService.removeUserFromGym(1L, userSub);
            System.out.println("User " + userSub + " left the gym");
            kafkaTemplate.send("reply-topic", correlationId, "Left");
            return;
        } else if (gymService.isGymFull(1L)) {
            System.out.println("Gym is full");
            kafkaTemplate.send("reply-topic", correlationId, "Full");
            return;
        } else {
            gymService.addUserToGym(1L, userSub);
            System.out.println("User " + userSub + " entered the gym");
            kafkaTemplate.send("reply-topic", correlationId, "Entered");
        }
    }

            

    
}
