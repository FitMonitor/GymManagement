package deti.fitmonitor.gyms.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Machine")
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    @JsonIgnoreProperties("machines")
    private Gym gym;

    private String description;

    @OneToMany(mappedBy = "machine")
    @JsonIgnoreProperties("machine")
    private List<Exercise> exercises;
}

