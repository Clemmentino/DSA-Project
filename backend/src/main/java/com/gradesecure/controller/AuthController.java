package com.gradesecure.controller;

import com.gradesecure.dto.LoginRequest;
import com.gradesecure.dto.LoginResponse;
import com.gradesecure.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login
     * Flowchart 3: input credentials → validate → valid? → role → dashboard
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/auth/me
     * Returns user info by ID (passed as query param for simplicity)
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestParam Long userId) {
        try {
            var user = authService.getUserById(userId);
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "role", user.getRole().name()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
