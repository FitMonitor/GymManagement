package deti.fitmonitor.gyms.repositories;

import deti.fitmonitor.gyms.models.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {
}

