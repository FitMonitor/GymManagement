package deti.fitmonitor.gyms.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.repositories.MachineRepository;


@DataJpaTest
class MachineRepositoryTest {

    @Autowired
    private MachineRepository machineRepository;

    @Test
    void testSaveMachine() {
        Machine machine = new Machine();
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setDescription("Description 1");

        machineRepository.save(machine);

        List<Machine> machines = machineRepository.findAll();
        assertThat(machines).hasSize(1);
    }

    @Test
    void testFindByUserSub() {
        Machine machine = new Machine();
        machine.setName("Machine A");
        machine.setAvailable(true);
        machine.setDescription("Description A");
        machine.setUserSub("feu1842572489ufjoekwj");
        machineRepository.save(machine);

        Optional<Machine> foundMachine = machineRepository.findByUserSub("feu1842572489ufjoekwj");

        assertThat(foundMachine).isPresent();
        assertThat(foundMachine.get().getName()).isEqualTo("Machine A");
        assertThat(foundMachine.get().getDescription()).isEqualTo("Description A");
        assertThat(foundMachine.get().isAvailable()).isTrue();
        assertThat(foundMachine.get().getUserSub()).isEqualTo("feu1842572489ufjoekwj");
    }
}