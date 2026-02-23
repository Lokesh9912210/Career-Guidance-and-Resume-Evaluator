package com.example.career.service;

import com.example.career.dto.LoginRequest;
import com.example.career.dto.RegisterRequest;
import com.example.career.model.User;
import com.example.career.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public Map<String, Object> register(RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByEmail(request.getEmail())) {
            response.put("success", false);
            response.put("message", "Email already registered");
            return response;
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .telegramChatId(request.getTelegramChatId())
                .build();

        userRepository.save(user);
        String token = generateToken(user.getEmail());

        response.put("success", true);
        response.put("message", "Registration successful");
        response.put("token", token);
        response.put("user", Map.of("id", user.getId(), "fullName", user.getFullName(), "email", user.getEmail()));
        return response;
    }

    public Map<String, Object> login(LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return response;
        }

        User user = userOpt.get();
        String token = generateToken(user.getEmail());

        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("user", Map.of("id", user.getId(), "fullName", user.getFullName(), "email", user.getEmail()));
        return response;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String generateToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }
}