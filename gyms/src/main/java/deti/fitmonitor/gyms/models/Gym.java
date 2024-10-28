package deti.fitmonitor.gyms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

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

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }


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