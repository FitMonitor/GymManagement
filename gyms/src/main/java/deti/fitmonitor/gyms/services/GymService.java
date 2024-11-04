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
}
