package com.gradesecure.service;

import com.gradesecure.dto.LoginRequest;
import com.gradesecure.dto.LoginResponse;
import com.gradesecure.model.User;
import com.gradesecure.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validate credentials and return user info with a simple token.
     * Maps to Flowchart 3 (Login): input credentials → validate → valid? → role check
     */
    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOpt.get();

        // Simple password check (in production, use BCrypt)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate a simple token (in production, use JWT)
        String token = UUID.randomUUID().toString();

        return new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getFullName(),
            user.getRole().name(),
            token
        );
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
