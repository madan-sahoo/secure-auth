package com.secureauth.service.impl;

import com.secureauth.dto.request.RegisterRequest;
import com.secureauth.dto.response.RegisterResponse;
import com.secureauth.entity.Role;
import com.secureauth.entity.User;
import com.secureauth.entity.UserRole;
import com.secureauth.exception.RoleNotFoundException;
import com.secureauth.exception.UserAlreadyExistException;
import com.secureauth.repository.RoleRepository;
import com.secureauth.repository.UserRepository;
import com.secureauth.repository.UserRoleRepository;
import com.secureauth.util.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.secureauth.util.Constant.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("madan");
        registerRequest.setEmail("madan@test.com");
        registerRequest.setPassword("password123");
    }

    // case: success
    @Test
    void register_success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);

        User savedUser = new User(registerRequest.getUsername(), registerRequest.getEmail(), "hashed-password");
        UUID userId = UUID.randomUUID();
        ReflectionTestUtils.setField(savedUser, "id", userId);
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        Role role = new Role();
        role.setName(ROLE_USER);
        when(roleRepository.findByName(ROLE_USER)).thenReturn(Optional.of(role));

        RegisterResponse response = userService.register(registerRequest);
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("madan", response.getUsername());
        assertEquals(Set.of(ROLE_USER), response.getRoles());

        verify(userRepository).save(any(User.class));
        verify(userRoleRepository).save(any(UserRole.class));
    }

    // case: user exist
    @Test
    void register_userAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
        assertThrows(UserAlreadyExistException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any());
        verify(userRoleRepository, never()).save(any());
    }

    //case: role not found
    @Test
    void register_roleNotFound() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                "hashed-password"
        ));

        when(roleRepository.findByName(ROLE_USER)).thenReturn(Optional.empty());
        assertThrows(RoleNotFoundException.class, () -> userService.register(registerRequest));
        verify(userRoleRepository, never()).save(any());
    }

    // case: database failure
    @Test
    void register_databaseFailure() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
        verify(userRoleRepository, never()).save(any());
    }

    // case: password validation
    @Test
    void register_passwordIsHashed() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Role role = new Role();
        role.setName(ROLE_USER);

        when(roleRepository.findByName(ROLE_USER)).thenReturn(Optional.of(role));

        userService.register(registerRequest);
        User savedUser = userCaptor.getValue();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(
                encoder.matches(
                    registerRequest.getPassword(),
                    savedUser.getPasswordHash()
                )
        );
    }
}