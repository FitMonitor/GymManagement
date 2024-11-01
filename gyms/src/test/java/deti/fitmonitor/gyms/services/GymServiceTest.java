package deti.fitmonitor.gyms.services;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.repositories.GymRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GymServiceTests {

    private GymService gymService;
    private GymRepository gymRepository;

    @BeforeEach
    void setUp() {
        gymRepository = mock(GymRepository.class);
        gymService = new GymService(gymRepository);
    }

    @Test
    void createGym_WhenGymDoesNotExist_ShouldCreateGym() {
        String gymName = "Test Gym";
        int capacity = 100;

        when(gymRepository.findByGymName(gymName)).thenReturn(Optional.empty());
        when(gymRepository.save(any(Gym.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Gym gym = gymService.createGym(gymName, capacity);

        assertNotNull(gym);
        assertEquals(gymName, gym.getGymName());
        assertEquals(capacity, gym.getCapacity());
        verify(gymRepository).save(any(Gym.class));
    }

    @Test
    void createGym_WhenGymExists_ShouldThrowException() {
        String gymName = "Existing Gym";
        int capacity = 100;

        when(gymRepository.findByGymName(gymName)).thenReturn(Optional.of(new Gym()));

        Exception exception = assertThrows(RuntimeException.class, () -> gymService.createGym(gymName, capacity));
        assertEquals("Gym already exists", exception.getMessage());
    }

    @Test
    void getGym_WhenGymExists_ShouldReturnGym() {
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setGymName("teste");
        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        Gym foundGym = gymService.getGymByID(1L);

        assertEquals(gym.getGymId(), foundGym.getGymId());
        assertEquals(gym.getGymName(), foundGym.getGymName());
    }


}
