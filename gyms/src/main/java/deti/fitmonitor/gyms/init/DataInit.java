package deti.fitmonitor.gyms.init;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.repositories.GymRepository;
import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.repositories.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test & !integration-test")
public class DataInit implements CommandLineRunner{

    private GymRepository gymRepository;
    private MachineRepository machineRepository;

    @Autowired
    public DataInit(GymRepository gymRepository, MachineRepository machineRepository){
        this.gymRepository = gymRepository;
        this.machineRepository = machineRepository;
    }

    public void run(String... args) throws Exception {
        loadGym();
        loadMachine();

    }

    private void loadGym(){
        Gym gym = new Gym();
        gym.setGymName("Fit Arena");
        gym.setCapacity(100);
        gym.setOccupancy(10);
        gymRepository.save(gym);
    }

    private void loadMachine(){
        Machine machine = new Machine();
        machine.setName("Machine 1");
        machine.setAvailable(true);
        machine.setDescription("Description 1");
        machineRepository.save(machine);
    }
}
