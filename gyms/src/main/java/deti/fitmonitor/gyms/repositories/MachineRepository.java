package deti.fitmonitor.gyms.repositories;

import deti.fitmonitor.gyms.models.Machine;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository  extends JpaRepository<Machine, Long> {

    //get machine by userSub
    Optional<Machine> findByUserSub(String userSub);
}
