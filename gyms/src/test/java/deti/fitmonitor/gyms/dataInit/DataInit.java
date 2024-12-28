package deti.fitmonitor.gyms.dataInit;

import deti.fitmonitor.gyms.models.*;
import deti.fitmonitor.gyms.repositories.GymRepository;
import deti.fitmonitor.gyms.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("test")
public class DataInit implements CommandLineRunner {
    private MachineService machineService;
    private GymRepository gymRepository;

    @Autowired
    public DataInit(MachineService machineService,GymRepository gymRepository) {
        this.machineService = machineService;
        this.gymRepository = gymRepository;
    }

    public void run(String... args) throws Exception {
        this.loadData();
    }

    private void loadData(){
        System.out.println("dsfnjdghofdjmoikdrjmhoikdrj");
        Machine machine1 = new Machine();
        machine1.setId(1L);
        machine1.setName("Machine 1");
        machine1.setAvailable(true);
        machine1.setDescription("Description 1");

        machineService.createMachine(machine1);

        Machine machine2 = new Machine();
        machine2.setId(2L);
        machine2.setName("Machine 2");
        machine2.setAvailable(false);
        machine2.setDescription("Description 2");

        machineService.createMachine(machine2);

        Machine machine3 = new Machine();
        machine3.setId(3L);
        machine3.setName("Machine 3");
        machine3.setAvailable(true);
        machine3.setDescription("Description 3");

        machineService.createMachine(machine3);

        Machine machine4 = new Machine();
        machine4.setId(4L);
        machine4.setName("Machine 4");
        machine4.setAvailable(false);
        machine4.setDescription("Description 4");

        machineService.createMachine(machine4);

        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setGymName("Fit Arena");
        gym.setCapacity(100);
        gym.setOccupancy(10);
        gymRepository.save(gym);
    }

    
}
