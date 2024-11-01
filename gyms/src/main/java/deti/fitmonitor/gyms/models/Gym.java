package deti.fitmonitor.gyms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gyms")
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gymId;
    private String gymName;
    private int capacity;
    @JsonProperty("occupancy")
    private int occupancy;


    public void checkInUpdate() {
        this.occupancy++;
    }

    public  void checkOutUpdate() {
        this.occupancy--;
    }

    public Long getGymId() {
        return gymId;
    }
}