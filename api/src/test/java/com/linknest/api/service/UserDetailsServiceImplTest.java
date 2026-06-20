package com.linknest.api.service;

import com.linknest.api.model.User;
import com.linknest.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    // Test 7: Loading an existing user returns correct UserDetails
    @Test
    void loadUserByUsername_ExistingUser_ShouldReturnUserDetails() {
        User mockUser = new User();
        mockUser.setUsername("yajnesh");
        mockUser.setPassword("$2a$10$hashedpassword");
        mockUser.setIsAdmin(false);

        when(userRepository.findByUsername("yajnesh")).thenReturn(Optional.of(mockUser));

        UserDetails result = userDetailsService.loadUserByUsername("yajnesh");

        assertNotNull(result);
        assertEquals("yajnesh", result.getUsername());
        assertEquals("$2a$10$hashedpassword", result.getPassword());
        // Verify the repository was called exactly once
        verify(userRepository, times(1)).findByUsername("yajnesh");
    }

    // Test 8: Loading a non-existent user throws UsernameNotFoundException
    @Test
    void loadUserByUsername_NonExistentUser_ShouldThrowException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("ghost"),
                "Should throw UsernameNotFoundException for unknown username");

        verify(userRepository, times(1)).findByUsername("ghost");
    }
}
