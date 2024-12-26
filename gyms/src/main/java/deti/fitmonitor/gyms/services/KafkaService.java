package deti.fitmonitor.gyms.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import deti.fitmonitor.gyms.services.MachineService;

@Service
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final MachineService machineService;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate, MachineService machineService) {
        this.kafkaTemplate = kafkaTemplate;
        this.machineService = machineService;
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
    
}
