package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.services.MachineService;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

//cors
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/machine")
@Tag(name = "movies", description = "Endpoints to manage movies")
public class MachineController {
    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @PostMapping
    @Operation(summary = "Create a new machine")
    public ResponseEntity<Machine> createMachine(@RequestBody Machine machine) {
        machineService.createMachine(machine);
        return new ResponseEntity<>(machine, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get a machine by id")
    public ResponseEntity<Machine> getMachine(@RequestParam Long id) {
        Machine machine = machineService.getMachine(id);
        return ResponseEntity.ok(machine);
    }

    @GetMapping
    @RequestMapping("/all")
    public ResponseEntity<List<Machine>>  getMachines() {
        List<Machine> machines = machineService.getAllMachines();
        return ResponseEntity.ok(machines);
    }

}