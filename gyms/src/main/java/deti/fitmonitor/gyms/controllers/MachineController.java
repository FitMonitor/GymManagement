package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.services.MachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/machine")
public class MachineController {
    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @PostMapping
    public ResponseEntity<Machine> createMachine(@RequestBody Machine machine) {
        machineService.createMachine(machine);
        return ResponseEntity.ok(machine);
    }

    @GetMapping
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