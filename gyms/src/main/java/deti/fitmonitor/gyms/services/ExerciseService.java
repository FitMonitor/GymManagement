package deti.fitmonitor.gyms.services;

import deti.fitmonitor.gyms.enums.ExerciseCategory;
import deti.fitmonitor.gyms.enums.MuscleGroup;
import deti.fitmonitor.gyms.models.Exercise;
import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.repositories.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {
    @Autowired
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository){this.exerciseRepository=exerciseRepository;}

    public Exercise getExerciseByID(Long id){
        return exerciseRepository.findById(id).orElse(null);
    }

    public Exercise getExerciseByName(String name){
        return exerciseRepository.findByExerciseName(name).orElse(null);
    }

    public List<Exercise> getAllExercises(){
        return exerciseRepository.findAll();
    }

    public Exercise createExercise(String exerciseName, Machine machine, String category,String muscleGroup, String description) {

        for (Exercise exercise: machine.getExercises()){
            if (exercise.getExerciseName().equals(exerciseName)) {
                throw new RuntimeException("Exercise ("+exerciseName+") in this machine already exists");
            }
        }

        Exercise exercise = new Exercise();
        exercise.setExerciseName(exerciseName);
        exercise.setExerciseCategory(ExerciseCategory.valueOf(category));
        exercise.setMuscleGroup(MuscleGroup.valueOf(muscleGroup));
        exercise.setDescription(description);
        exercise.setMachine(machine);
        Exercise savedExercise = exerciseRepository.save(exercise);

        machine.getExercises().add(savedExercise);
        return savedExercise;
    }
}
