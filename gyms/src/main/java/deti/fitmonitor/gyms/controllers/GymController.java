package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.services.GymService;
import deti.fitmonitor.gyms.services.MachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/gym")
@Tag(name = "gyms", description = "Endpoints to manage gyms")
public class GymController {
    private final GymService gymService;

    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @PostMapping
    @Operation(summary = "Create a new gym")
    public ResponseEntity<Gym> createGym(@RequestBody Gym gym) {
        gymService.createGym(gym);
        return ResponseEntity.ok(gym);
    }

    @GetMapping
    @Operation(summary = "Get the gym information")
    public ResponseEntity<List<Gym>> getGym() {
        List<Gym> gyms = gymService.getGym();
        return ResponseEntity.ok(gyms);
    }

}
