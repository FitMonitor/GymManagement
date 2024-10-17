package deti.fitmonitor.gyms.services;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.repositories.GymRepository;
import deti.fitmonitor.gyms.repositories.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GymService {
    private final GymRepository gymRepository;
    private final MachineRepository machineRepository;

    private GymService(GymRepository gymRepository, MachineRepository machineRepository) {
        this.gymRepository = gymRepository;
        this.machineRepository = machineRepository;
    }

    public void createGym(Gym gym) {
        List<Machine> machines = new ArrayList<>();

        for (Machine machine : gym.getMachines()) {
            Machine existingMachine = machineRepository.findById(machine.getId())
                    .orElseThrow(() -> new RuntimeException("Machine not found"));
            existingMachine.setGym(gym);
            machines.add(existingMachine);
        }

        gym.setMachines(machines);
        gymRepository.save(gym);
    }

    public Optional<Gym> updateGym(Gym gym) {
        return Optional.of(gymRepository.save(gym));
    }

    public List<Gym> getGym(){
        return gymRepository.findAll();
    }
}
