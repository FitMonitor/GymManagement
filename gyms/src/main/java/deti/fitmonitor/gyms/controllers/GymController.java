package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.services.GymService;
import deti.fitmonitor.gyms.services.MachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gym")
public class GymController {
    private final GymService gymService;

    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @PostMapping
    public ResponseEntity<Gym> createGym(@RequestBody Gym gym) {
        gymService.createGym(gym);
        return ResponseEntity.ok(gym);
    }

    @GetMapping
    public ResponseEntity<List<Gym>> getGym() {
        List<Gym> gyms = gymService.getGym();
        return ResponseEntity.ok(gyms);
    }

}
