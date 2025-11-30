package ru.nsu.neuropsychologist.neuro_psychologist_backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AuthResponse;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.LoginRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.RegisterRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.entity.User;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.repository.UserRepository;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.security.JwtUtil;

@Service
public class AuthService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil,
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser);
        
        return new AuthResponse(
                token,
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getId()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        // Load user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getId()
        );
    }
}