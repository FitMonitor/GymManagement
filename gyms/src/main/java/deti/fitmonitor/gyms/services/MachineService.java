package deti.fitmonitor.gyms.services;

import deti.fitmonitor.gyms.models.Machine;
import deti.fitmonitor.gyms.repositories.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MachineService {
    private final MachineRepository machineRepository;

    private MachineService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public Machine createMachine(Machine machine) {
        return machineRepository.save(machine);
    }

    public Machine getMachine(Long id) {
        return machineRepository.findById(id).orElse(null);
    }

    public List<Machine> getAllMachines(){
        return machineRepository.findAll();
    }

}

