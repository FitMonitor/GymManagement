package deti.fitmonitor.gyms.repositories;

import deti.fitmonitor.gyms.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByExerciseName(String exerciseName);
    List<Exercise> findByMachineId(Long machineId);
    List<Exercise> findByMachineName(String machineName);
}
