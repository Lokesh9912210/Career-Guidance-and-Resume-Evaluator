package com.example.career.controller;

import com.example.career.dto.LoginRequest;
import com.example.career.dto.RegisterRequest;
import com.example.career.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> response = authService.register(request);
        return (boolean) response.get("success") ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> response = authService.login(request);
        return (boolean) response.get("success") ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = authService.getUserByEmail(email);
            return ResponseEntity.ok(Map.of("success", true, "user", Map.of("id", user.getId(), "fullName", user.getFullName(), "email", user.getEmail())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}