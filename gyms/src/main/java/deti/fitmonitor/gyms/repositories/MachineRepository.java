package deti.fitmonitor.gyms.repositories;

import deti.fitmonitor.gyms.GymsApplication;
import deti.fitmonitor.gyms.models.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository  extends JpaRepository<Machine, Long> {
}
