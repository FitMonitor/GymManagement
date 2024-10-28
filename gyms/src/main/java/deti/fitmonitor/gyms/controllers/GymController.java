package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.services.GymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/gyms")
public class GymController {

    @Autowired
    private GymService gymService;

    @PostMapping("/create")
    public ResponseEntity<Gym> createGym(@RequestBody Map<String, String> requestBody) {
        if(!requestBody.containsKey("gymName")||!requestBody.containsKey("capacity")){
            return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        String gymName = requestBody.get("gymName");
        int capacity = Integer.parseInt(requestBody.get("capacity"));
        Gym gym = gymService.createGym(gymName,capacity);
        return new ResponseEntity<>(gym, HttpStatus.CREATED);
    }

    @PostMapping("/")
    public ResponseEntity<Gym> getGym(@RequestBody Map<String, String> requestBody) {
        String gymName = requestBody.get("gymName");
        Gym gym = gymService.getGym(gymName);
        return new ResponseEntity<>(gym, HttpStatus.OK);
    }

    @PostMapping("/checkInUpdate")
    public ResponseEntity<Gym> checkInUpdate(@RequestBody Map<String, String> requestBody) {
        Long gymId = Long.parseLong(requestBody.get("gymId"));

        gymService.checkInUpdate(gymId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/checkOutUpdate")
    public ResponseEntity<Gym> checkOutUpdate(@RequestBody Map<String, String> requestBody) {
        Long gymId = Long.parseLong(requestBody.get("gymId"));

        gymService.checkOutUpdate(gymId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/occupancy")
    public ResponseEntity<Gym> getGymOccupancy(@RequestBody Map<String, String> requestBody) {
        if(!requestBody.containsKey("gymId")){
            return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Long gymId = Long.parseLong(requestBody.get("gymId"));
        Gym gym = gymService.getOccupancy(gymId);
        return new ResponseEntity<>(gym, HttpStatus.OK);
    }
}
