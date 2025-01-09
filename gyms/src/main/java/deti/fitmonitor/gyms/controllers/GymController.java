package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.services.GymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@CrossOrigin(origins = "https://es-ua.ddns.net")
@RestController
@RequestMapping("/default/api/gyms")
public class GymController {

    @Autowired
    private GymService gymService;

    @GetMapping()
    @Operation(summary = "Get gym by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gym found"),
        @ApiResponse(responseCode = "404", description = "Gym not found")
    })
    public ResponseEntity<Gym> getGym(@RequestParam Long id) {
        Gym gym = gymService.getGymByID(id);
        if (gym == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(gym);
    }


    @GetMapping("/occupancy")
    @Operation(summary = "Get gym occupancy by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gym occupancy found"),
        @ApiResponse(responseCode = "404", description = "Gym not found")
    })
    public ResponseEntity<Integer> getGymOccupancy(@RequestParam Long id) {
        Integer occupancy = gymService.getOccupancy(id);
        if (occupancy == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(occupancy, HttpStatus.OK);
    }
}
