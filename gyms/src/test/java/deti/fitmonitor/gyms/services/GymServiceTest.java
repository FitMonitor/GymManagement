package deti.fitmonitor.gyms.services;

import deti.fitmonitor.gyms.models.Gym;
import deti.fitmonitor.gyms.repositories.GymRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class GymServiceTest {

    @Mock
    private GymRepository gymRepository;

    @InjectMocks
    private GymService gymService;

    @Test
    void test_CreateGym_WhenGymDoesNotExist_ShouldCreateGym() {
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setGymName("Gym 1");
        gym.setCapacity(100);
        gym.setOccupancy(0);

        when(gymRepository.findByGymName("Gym 1")).thenReturn(Optional.empty());
        when(gymRepository.save(any(Gym.class))).thenReturn(gym);

       Gym savedGym = gymService.createGym(gym.getGymName(),gym.getCapacity());

        assertNotNull(savedGym);
        assertEquals("Gym 1", savedGym.getGymName());
        assertEquals(100, savedGym.getCapacity());
        assertEquals(0, savedGym.getOccupancy());

        verify(gymRepository, times(1)).findByGymName("Gym 1");
        verify(gymRepository, times(1)).save(any(Gym.class));
    }

    @Test
    void test_CreateGym_WhenGymExists_ShouldThrowException() {
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setGymName("Gym 1");
        gym.setCapacity(100);
        gym.setOccupancy(0);

        when(gymRepository.findByGymName("Gym 1")).thenReturn(Optional.of(gym));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            gymService.createGym(gym.getGymName(), gym.getCapacity());
        }, "Expected createGym() to throw an exception when gym already exists");

        assertEquals("Gym already exists", thrown.getMessage(), "The exception message should match");

        verify(gymRepository, times(1)).findByGymName("Gym 1");
        verify(gymRepository, never()).save(any(Gym.class));
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

    @Test
    void test_GetOccupancy_WhenGymExists_ShouldReturnOccupancy() {
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setOccupancy(50);

        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        Integer occupancy = gymService.getOccupancy(1L);

        assertNotNull(occupancy);
        assertEquals(50, occupancy);
        verify(gymRepository, times(1)).findById(1L);
    }

    @Test
    void test_IsGymFull_WhenGymIsFull_ShouldReturnTrue() {
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setOccupancy(100);
        gym.setCapacity(100);

        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        Boolean isFull = gymService.isGymFull(1L);

        assertNotNull(isFull);
        assertTrue(isFull);
        verify(gymRepository, times(1)).findById(1L);
    }

    @Test
    void test_IsGymFull_WhenGymIsNotFull_ShouldReturnFalse() {
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setOccupancy(50);
        gym.setCapacity(100);

        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        Boolean isFull = gymService.isGymFull(1L);

        assertNotNull(isFull);
        assertFalse(isFull);
        verify(gymRepository, times(1)).findById(1L);
    }

    @Test
    void test_IsUserInGym_WhenUserIsInGym_ShouldReturnTrue() {
        Long gymId = 1L;
        String userSub = "user123";
        Gym gym = new Gym();
        gym.setGymId(gymId);
        gym.setUsersInGym(new ArrayList<>(List.of(userSub)));

        when(gymRepository.findById(gymId)).thenReturn(Optional.of(gym));

        Boolean isInGym = gymService.isUserInGym(gymId, userSub);

        assertNotNull(isInGym);
        assertTrue(isInGym);
        verify(gymRepository, times(1)).findById(gymId);
    }

    @Test
    void test_IsUserInGym_WhenUserIsNotInGym_ShouldReturnFalse() {
        String userSub = "user123";
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setUsersInGym(new ArrayList<>());

        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        Boolean isInGym = gymService.isUserInGym(1L, userSub);

        assertNotNull(isInGym);
        assertFalse(isInGym);
        verify(gymRepository, times(1)).findById(1L);
    }

    @Test
    void test_AddUserToGym_WhenGymExists_ShouldAddUserAndIncreaseOccupancy() {
        String userSub = "user123";
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setOccupancy(0);
        gym.setCapacity(100);
        gym.setUsersInGym(new ArrayList<>());

        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        gymService.addUserToGym(1L, userSub);

        assertTrue(gym.getUsersInGym().contains(userSub));
        assertEquals(1, gym.getOccupancy());
        verify(gymRepository, times(1)).findById(1L);
        verify(gymRepository, times(1)).save(gym);
    }

    @Test
    void test_RemoveUserFromGym_WhenGymExists_ShouldRemoveUserAndDecreaseOccupancy() {
        String userSub = "user123";
        Gym gym = new Gym();
        gym.setGymId(1L);
        gym.setOccupancy(1);
        gym.setCapacity(100);
        gym.setUsersInGym(new ArrayList<>(List.of(userSub)));

        when(gymRepository.findById(1L)).thenReturn(Optional.of(gym));

        gymService.removeUserFromGym(1L, userSub);

        assertFalse(gym.getUsersInGym().contains(userSub));
        assertEquals(0, gym.getOccupancy());
        verify(gymRepository, times(1)).findById(1L);
        verify(gymRepository, times(1)).save(gym);
    }





}
