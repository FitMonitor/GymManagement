package deti.fitmonitor.gyms.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.repositories.GymRepository;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
class GymRepositoryTest {

    @Autowired
    private GymRepository gymRepository;

    @Test
    void testFindGymByName(){
        Gym gym = new Gym();
        gym.setGymName("Gym 1");
        gym.setOccupancy(30);
        gym.setCapacity(100);

        gymRepository.save(gym);

        Optional<Gym> foundGym = gymRepository.findByGymName("Gym 1");

        assertThat(foundGym).isPresent();
        assertThat(foundGym.get().getGymName()).isEqualTo("Gym 1");
        assertThat(foundGym.get().getOccupancy()).isEqualTo(30);

    }

}
