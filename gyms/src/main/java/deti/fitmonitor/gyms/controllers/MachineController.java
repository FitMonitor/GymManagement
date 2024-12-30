package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.services.GymService;
import deti.fitmonitor.gyms.services.MachineService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;

//cors
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/machine")
@Tag(name = "machines", description = "Endpoints to manage machines")
public class MachineController {
    private final MachineService machineService;
    private final GymService gymService;

    public MachineController(MachineService machineService,GymService gymService) {
        this.machineService = machineService;
        this.gymService = gymService;

    }

    @PostMapping
    @Operation(summary = "Create a new machine")
    public ResponseEntity<Machine> createMachine(
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("available") boolean available,
        @RequestParam("image") MultipartFile image) {
            
        Gym gym = gymService.getGymByID(1L);

        Machine machine = new Machine(name, description, available,gym);
        
        try {

            String imagePath = saveImage(image);
            machine.setImagePath(imagePath);
            
            machineService.createMachine(machine);
            
            return ResponseEntity.ok(machine);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        // Define the directory to store the uploaded images
        Path uploadDirectory = Paths.get("uploads/");
    
        // Create the directory if it doesn't exist
        Files.createDirectories(uploadDirectory); // This ensures the directory exists
    
        // Define the path where the image will be saved
        String imageName = image.getOriginalFilename();
        Path filePath = uploadDirectory.resolve(imageName);
    
        // Save the image to the file system
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    
        return filePath.toString(); // Return the path to the saved image
    }


    @GetMapping
    @Operation(summary = "Get a machine by id")
    public ResponseEntity<Machine> getMachine(@RequestParam Long id) {
        Machine machine = machineService.getMachine(id);
        return ResponseEntity.ok(machine);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Machine>>  getMachines() {
        List<Machine> machines = machineService.getAllMachines();
        return ResponseEntity.ok(machines);
    }

    @GetMapping("/user")
    public ResponseEntity<Machine> getMachineByUserSub(@RequestParam String userSub) {
        Machine machine = machineService.getMachineByUserSub(userSub);
        return ResponseEntity.ok(machine);
    }

    @GetMapping("/image")
    @Operation(summary = "Get machine image by path")
    public ResponseEntity<byte[]> getMachineImage(@RequestParam String imagePath) {
        try {
            Path filePath = Paths.get("uploads/").resolve(imagePath);

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] imageBytes = Files.readAllBytes(filePath);

            String contentType = Files.probeContentType(filePath);

            return ResponseEntity.ok()
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .body(imageBytes);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}