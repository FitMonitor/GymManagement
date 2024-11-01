package deti.fitmonitor.gyms.repositories;

import deti.fitmonitor.gyms.models.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {
    Optional<Gym> findByGymName(String gymName);
}
