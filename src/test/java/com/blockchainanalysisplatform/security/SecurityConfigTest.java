package com.blockchainanalysisplatform.security;

import com.blockchainanalysisplatform.Configs.SecurityConfig;
import com.blockchainanalysisplatform.Data.User;
import com.blockchainanalysisplatform.RepositoriesJPA.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testUserDetailsService_WhenUserExists_ThenReturnUser() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        SecurityConfig securityConfig = new SecurityConfig();
        UserDetailsService userDetailsService = securityConfig.userDetailsService(userRepository);

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
        //verify(userRepository, never()).findByEmail(username);
    }

    @Test
    void testUserDetailsService_WhenUserDoesNotExist_ThenThrowException() {
        // Arrange
        String username = "testuser";

        when(userRepository.findByUsername(username)).thenReturn(null);
        when(userRepository.findByEmail(username)).thenReturn(null);

        SecurityConfig securityConfig = new SecurityConfig();
        UserDetailsService userDetailsService = securityConfig.userDetailsService(userRepository);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(username);
    }
}
