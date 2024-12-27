package deti.fitmonitor.gyms.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import deti.fitmonitor.gyms.services.MachineService;
import deti.fitmonitor.gyms.services.GymService;
import deti.fitmonitor.gyms.services.JwtUtilService;

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
        if (machineService.isMachineAvailable(Long.parseLong(machineId), intention, userSub)) {
            response = "Machine " + machineId + " is now " + (intention.equals("use") ? "in use" : "available");
        } else {
            response = "Machine " + machineId + " is already in use";
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
            kafkaTemplate.send("reply-topic", correlationId, "User " + userSub + " left the gym");
            return;
        } else if (gymService.isGymFull(1L)) {
            System.out.println("Gym is full");
            kafkaTemplate.send("reply-topic", correlationId, "Gym is full");
            return;
        } else {
            gymService.addUserToGym(1L, userSub);
            System.out.println("User " + userSub + " entered the gym");
            kafkaTemplate.send("reply-topic", correlationId, "User " + userSub + " entered the gym");
        }
    }

            

    
}
