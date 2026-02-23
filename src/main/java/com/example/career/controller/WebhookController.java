package com.example.career.controller;

import com.example.career.model.User;
import com.example.career.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WebhookController {

    private final UserRepository userRepository;

    @PostMapping("/telegram-link")
    public ResponseEntity<?> linkTelegram(@RequestBody Map<String, String> payload) {
        Optional<User> userOpt = userRepository.findByEmail(payload.get("email"));
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setTelegramChatId(payload.get("chatId"));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("success", true, "message", "Linked"));
        }
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
    }
}