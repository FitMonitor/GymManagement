package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.services.GymService;
import deti.fitmonitor.gyms.services.MachineService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@CrossOrigin(origins = "https://es-ua.ddns.net")
@RestController
@RequestMapping("default/api/gyms/machine")
@Tag(name = "machines", description = "Endpoints to manage machines")
public class MachineController {
    private final MachineService machineService;
    private final GymService gymService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MachineController.class);

    public MachineController(MachineService machineService,GymService gymService, AmazonS3 amazonS3) {
        this.machineService = machineService;
        this.gymService = gymService;
        this.amazonS3 = amazonS3;

    }

    @PostMapping
    @Operation(summary = "Create a new machine")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Machine created"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Machine> createMachine(
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("available") boolean available,
        @RequestParam("image") MultipartFile image) {

        Gym gym = gymService.getGymByID(1L);
        Machine machine = new Machine(name, description, available, gym);

        try {
            // Upload image to S3 and get the URL
            String imageUrl = uploadImageToS3(image);
            machine.setImagePath(imageUrl);

            machineService.createMachine(machine);

            return ResponseEntity.ok(machine);
        } catch (IOException e) {
            log.error("Error uploading image to S3", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    String uploadImageToS3(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

        // Upload file to S3 bucket
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), null));

        // Return the public URL of the uploaded file
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    //delete machine by id
    @DeleteMapping
    @Operation(summary = "Delete a machine by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Machine deleted"),
        @ApiResponse(responseCode = "404", description = "Machine not found")
    })
    public ResponseEntity<Machine> deleteMachine(@RequestParam Long id) {
        Machine machine = machineService.getMachine(id);
        if (machine == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        boolean deleted = machineService.deleteMachine(id);
        if (deleted) {
            return ResponseEntity.ok(machine);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping
    @Operation(summary = "Get a machine by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Machine found"),
        @ApiResponse(responseCode = "404", description = "Machine not found")
    })
    public ResponseEntity<Machine> getMachine(@RequestParam Long id) {
        Machine machine = machineService.getMachine(id);
        return ResponseEntity.ok(machine);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all machines")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Machines found"),
    })
    public ResponseEntity<List<Machine>>  getMachines() {
        List<Machine> machines = machineService.getAllMachines();
        return ResponseEntity.ok(machines);
    }

    @GetMapping("/user")
    @Operation(summary = "Get machine by user sub")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Machine found"),
    })
    public ResponseEntity<Machine> getMachineByUserSub(@RequestParam String userSub) {
        Machine machine = machineService.getMachineByUserSub(userSub);
        return ResponseEntity.ok(machine);
    }

    @GetMapping("/image")
    @Operation(summary = "Get machine image by path")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image found"),
        @ApiResponse(responseCode = "404", description = "Image not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<byte[]> getMachineImage(@RequestParam String imagePath) {
        try {
            // Fetch image from S3 bucket
            String decodedImagePath = URLDecoder.decode(imagePath, StandardCharsets.UTF_8);
            String fileName = decodedImagePath.substring(decodedImagePath.lastIndexOf("/") + 1); // Extract file name
            System.out.println("ok");
            System.out.println(extractFileNameFromUrl(fileName));
            S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, fileName));
            System.out.println(s3Object);

            // Convert InputStream to byte array
            InputStream inputStream = s3Object.getObjectContent();
            byte[] imageBytes = inputStream.readAllBytes();

            System.out.println(imageBytes);

            String contentType = s3Object.getObjectMetadata().getContentType();

            return ResponseEntity.ok()
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .body(imageBytes);
        } catch (AmazonS3Exception e) {
            log.error("Error retrieving image from S3", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            log.error("Error reading image from S3", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String extractFileNameFromUrl(String imagePath) {
        return imagePath.substring(imagePath.lastIndexOf("/") + 1);
    }


}