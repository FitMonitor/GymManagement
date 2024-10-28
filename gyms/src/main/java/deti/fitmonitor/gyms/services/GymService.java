package deti.fitmonitor.gyms.services;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.repositories.GymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GymService {

    @Autowired
    public GymService(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }
    @Autowired
    private GymRepository gymRepository;

    public Gym createGym(String gymName, int capacity) {
        Optional<Gym> gymOpt = gymRepository.findByGymName(gymName);
        if (gymOpt.isPresent()) {
            throw new RuntimeException("Gym already exists");
        }
        Gym gym = new Gym();
        gym.setGymName(gymName);
        gym.setCapacity(capacity);
        gym.setOccupancy(0);

        return gymRepository.save(gym);
    }

    public Gym getGym(String gymName) {
        Optional<Gym> gymOpt = gymRepository.findByGymName(gymName);
        if (gymOpt.isEmpty()) {
            throw new RuntimeException("Gym doesn't exists");
        }
        return gymOpt.get();
    }

    public Gym getOccupancy(Long gymId){
        Optional<Gym> gym = gymRepository.findById(gymId);
        if (gym.isPresent()) {
            return gym.get();
        }
        else {
            throw new RuntimeException("Gym doesn't exists");
        }
    }

    public void checkInUpdate(Long gymId){
        Optional<Gym> gym = gymRepository.findById(gymId);
        if (gym.isPresent()) {
            gym.get().checkInUpdate();
        }
        else {
            throw new RuntimeException("Gym doesn't exists");
        }
        gymRepository.save(gym.get());
    }

    public void checkOutUpdate(Long gymId){
        Optional<Gym> gym = gymRepository.findById(gymId);
        if (gym.isPresent()) {
            gym.get().checkOutUpdate();
        }
        else {
            throw new RuntimeException("Gym doesn't exists");
        }
        gymRepository.save(gym.get());
    }

}
