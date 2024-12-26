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

    public Boolean isMachineAvailable(Long id, String intention, String userSub) {
        Machine machine = machineRepository.findById(id).orElse(null);
        if (machine == null) {
            return false;
        }

        if (intention.equals("use") && machine.getUserSub() == null && machine.isAvailable()) {
            machine.setUserSub(userSub);
            machine.setAvailable(false);
            machineRepository.save(machine);
            return true;
        } else if (intention.equals("use") && machine.getUserSub() != null) {
            return false;
        } else if (intention.equals("leave") && machine.getUserSub().equals(userSub)) {
            machine.setUserSub(null);
            machine.setAvailable(true);
            machineRepository.save(machine);
            return true;
        } else if (intention.equals("leave") && machine.getUserSub() != userSub) {
            return false;
        } else {
            return false;
        }
    }

}

