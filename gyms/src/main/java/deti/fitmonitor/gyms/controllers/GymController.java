package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.services.GymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/gyms")
public class GymController {

    @Autowired
    private GymService gymService;

    @PostMapping("/create")
    public ResponseEntity<Gym> createGym(@RequestBody Gym gym) {
        if (gym.getGymName() == null || gym.getCapacity() == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        gymService.createGym(gym.getGymName(), gym.getCapacity());
        return new ResponseEntity<>(gym, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<Gym> getGym(@RequestParam Long id) {
        Gym gym = gymService.getGymByID(id);
        if (gym == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(gym);
    }


    @GetMapping("/occupancy")
    public ResponseEntity<Integer> getGymOccupancy(@RequestParam Long id) {
        Integer occupancy = gymService.getOccupancy(id);
        if (occupancy == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(occupancy, HttpStatus.OK);
    }
}
