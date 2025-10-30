package com.linknest.api.controller;

import com.linknest.api.model.User;
import com.linknest.api.dto.LoginRequest;
import com.linknest.api.dto.RegisterRequest;
import com.linknest.api.repository.UserRepository;
import com.linknest.api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setIsAdmin(false); // Default to non-admin

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // If authentication is successful, get the username
        String username = authentication.getName();

        // Fetch the full User object from the repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in repository"));

        // Generate the JWT using the User object
        String jwt = jwtUtil.generateToken(user);

        // Return the JWT in the response
        return ResponseEntity.ok(java.util.Collections.singletonMap("token", jwt));
    }

    // TEMPORARY ENDPOINT - REMOVE AFTER USING!
    @PostMapping("/make-admin/{username}")
    public ResponseEntity<?> makeAdmin(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsAdmin(true);
        userRepository.save(user);
        return ResponseEntity.ok("User " + username + " is now admin");
    }
}