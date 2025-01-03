package deti.fitmonitor.gyms.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;


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

    @Column(nullable = true)
    private String userSub;

    private String imagePath;

    public Machine(String name, String description, boolean available, Gym gym) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.gym = gym;
    }
}

