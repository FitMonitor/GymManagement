package deti.fitmonitor.gyms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import deti.fitmonitor.gyms.enums.ExerciseCategory;
import deti.fitmonitor.gyms.enums.MuscleGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Exercise")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exerciseName;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    @JsonIgnoreProperties("exercises")
    private Machine machine;

    private String description;

    private ExerciseCategory exerciseCategory;

    private MuscleGroup muscleGroup;


}
