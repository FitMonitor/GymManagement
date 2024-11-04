package deti.fitmonitor.gyms.init;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.repositories.GymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test & !integration-test")
public class DataInit implements CommandLineRunner{

    private GymRepository gymRepository;

    @Autowired
    public DataInit(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    public void run(String... args) throws Exception {
        loadGym();

    }

    private void loadGym(){
        Gym gym = new Gym();
        gym.setGymName("Fit Arena");
        gym.setCapacity(100);
        gym.setOccupancy(10);
        gymRepository.save(gym);
    }
}
