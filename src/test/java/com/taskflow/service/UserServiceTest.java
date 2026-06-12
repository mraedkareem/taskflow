package com.taskflow.service;

import com.taskflow.model.User;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void register_scramblesPasswordAndSetsUserRole() {
        User input = new User();
        input.setUsername("alice");
        input.setEmail("alice@test.com");
        input.setPassword("plain123");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("plain123")).thenReturn("SCRAMBLED");
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        User saved = userService.register(input);

        assertEquals("SCRAMBLED", saved.getPassword());
        assertEquals(User.Role.USER, saved.getRole());
    }

    @Test
    void register_throwsWhenUsernameAlreadyTaken() {
        User input = new User();
        input.setUsername("bob");
        input.setEmail("bob@test.com");
        input.setPassword("x");

        when(userRepository.existsByUsername("bob")).thenReturn(true);

        RuntimeException error = assertThrows(RuntimeException.class, () -> userService.register(input));

        assertEquals("Username already taken", error.getMessage());
        verify(userRepository, never()).save(any());
    }
}
