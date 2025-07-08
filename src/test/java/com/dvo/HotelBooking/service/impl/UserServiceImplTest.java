package com.dvo.HotelBooking.service.impl;

import com.dvo.HotelBooking.entity.RoleType;
import com.dvo.HotelBooking.entity.User;
import com.dvo.HotelBooking.entity.kafka.UserEvent;
import com.dvo.HotelBooking.exception.EntityExistsException;
import com.dvo.HotelBooking.exception.EntityNotFoundException;
import com.dvo.HotelBooking.mapper.UserMapper;
import com.dvo.HotelBooking.repository.UserRepository;
import com.dvo.HotelBooking.web.model.request.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "topicName", "test-topic");

        user = User.builder().username("name").email("email@email.ru").password("12345").build();
    }

    @Test
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("name")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("name");

        assertEquals("name", result.getUsername());
        assertEquals("email@email.ru", result.getEmail());
        verify(userRepository, times(1)).findByUsername(any());
    }

    @Test
    void testFindByUsername_whenNotFound() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("name"));
    }

    @Test
    void testExistsByUsernameAndEmail() {
        when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(true);

        boolean result = userService.existsByUsernameAndEmail(anyString(), anyString());

        assertTrue(result);
    }

    @Test
    void testCreate() {
        when(userRepository.findByUsername("name")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("email@email.ru")).thenReturn(false);
        when(passwordEncoder.encode("12345")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.create(user, RoleType.ROLE_USER);

        assertEquals("name", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertEquals(RoleType.ROLE_USER, result.getRoleType());
        verify(kafkaTemplate).send(eq("test-topic"), any(UserEvent.class));
    }

    @Test
    void testCreate_whenExistsByUsername() {
        when(userRepository.findByUsername("name")).thenReturn(Optional.of(user));

        assertThrows(EntityExistsException.class, () -> userService.create(user, RoleType.ROLE_USER));
    }

    @Test
    void testCreate_whenExistsByEmail() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.create(user, RoleType.ROLE_USER));
    }

    @Test
    void testUpdate() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("new@mail.ru")
                .password("12345")
                .build();

        User existedUser = new User();
        when(userRepository.findByUsername("name")).thenReturn(Optional.of(existedUser));
        when(passwordEncoder.encode("12345")).thenReturn("encoded");
        when(userRepository.save(existedUser)).thenReturn(existedUser);

        User result = userService.update(request, "name");
        verify(userMapper).updateRequestToUser(request, existedUser);
        assertEquals(existedUser, result);
    }

    @Test
    void testUpdate_whenUserNotFound() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("new@mail.ru")
                .password("12345")
                .build();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(request, "name"));
    }

    @Test
    void testDeleteById() {
        userService.deleteById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteByUsername() {
        userService.deleteByUsername("name");
        verify(userRepository).deleteByUsername("name");
    }


}
