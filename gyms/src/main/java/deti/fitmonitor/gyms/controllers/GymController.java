package deti.fitmonitor.gyms.controllers;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;


//cors
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/gym")
@Tag(name = "gyms", description = "Endpoints to manage gyms")
public class GymController {
    

}
