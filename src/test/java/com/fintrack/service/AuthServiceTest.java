package com.fintrack.service;

import com.fintrack.dto.request.LoginRequest;
import com.fintrack.dto.request.RegisterRequest;
import com.fintrack.dto.response.AuthResponse;
import com.fintrack.exception.BadRequestException;
import com.fintrack.model.User;
import com.fintrack.model.enums.UserRole;
import com.fintrack.repository.UserRepository;
import com.fintrack.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 3600000L);

        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("test-token");
        doNothing().when(categoryService).createDefaultCategories(any(User.class));

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("john@example.com");

        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(categoryService).createDefaultCategories(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email already in use");

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("test-token");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("john@example.com");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
        verify(userRepository).findByEmail("john@example.com");
    }
}
