package deti.fitmonitor.gyms.models;


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
    private Gym gym;

    private String description;
}

