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

    public Gym getGymByID(Long id) {
        return gymRepository.findById(id).orElse(null);
        
    }

    public Integer getOccupancy(Long gymId) {
        return gymRepository.findById(gymId)
                .map(Gym::getOccupancy)
                .orElse(null);
    }

    public Boolean isGymFull(Long gymId) {
        return gymRepository.findById(gymId)
                .map(gym -> gym.getOccupancy() >= gym.getCapacity())
                .orElse(null);
    }

    public Boolean isUserInGym(Long gymId, String userSub) {
        return gymRepository.findById(gymId)
                .map(gym -> gym.getUsersInGym().contains(userSub))
                .orElse(null);
    }

    public void addUserToGym(Long gymId, String userSub) {
        gymRepository.findById(gymId).ifPresent(gym -> {
            gym.getUsersInGym().add(userSub);
            gym.setOccupancy(gym.getOccupancy() + 1);
            gymRepository.save(gym);
        });
    }

    public void removeUserFromGym(Long gymId, String userSub) {
        gymRepository.findById(gymId).ifPresent(gym -> {
            gym.getUsersInGym().remove(userSub);
            gym.setOccupancy(gym.getOccupancy() - 1);
            gymRepository.save(gym);
        });
    }
}
