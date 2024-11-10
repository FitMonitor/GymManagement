package deti.fitmonitor.gyms.controllers;

import deti.fitmonitor.gyms.enums.ExerciseCategory;
import deti.fitmonitor.gyms.enums.MuscleGroup;
import deti.fitmonitor.gyms.models.Exercise;
import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.services.ExerciseService;
import deti.fitmonitor.gyms.services.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;
    @Autowired
    private MachineService machineService;


    @GetMapping("/all")
    public ResponseEntity<List<Exercise>> getExercises() {
        List<Exercise> exercises = exerciseService.getAllExercises();

        return ResponseEntity.ok(exercises);
    }

    @GetMapping()
    public ResponseEntity<Exercise> getExercise(@RequestBody Map<String, String> requestBody) {
        Exercise exercise = exerciseService.getExerciseByID(Long.parseLong(requestBody.get("exerciseId")));
        if (exercise == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(exercise);
    }


    @PostMapping("/create")
    public ResponseEntity<Exercise> createExercise(@RequestBody Map<String, String> requestBody) {
        String exerciseName = requestBody.get("exerciseName");
        Machine machine= machineService.getMachine(Long.parseLong(requestBody.get("machineId")));
        String category=requestBody.get("category");
        String muscleGroup=requestBody.get("muscleGroup");
        String description=requestBody.get("description");

        Exercise exercise=exerciseService.createExercise(exerciseName,machine,category,muscleGroup,description);
        return new ResponseEntity<>(exercise, HttpStatus.CREATED);
    }

    @GetMapping("/categories")
    public List<ExerciseCategory> getExerciseCategories() {
        return Arrays.asList(ExerciseCategory.values());
    }

    @GetMapping("/musclesGroups")
    public List<MuscleGroup> getMuscleGroups() {
        return Arrays.asList(MuscleGroup.values());
    }


}
