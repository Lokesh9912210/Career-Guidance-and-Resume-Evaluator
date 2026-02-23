package com.example.career.controller;

import com.example.career.model.User;
import com.example.career.service.AuthService;
import com.example.career.service.OnetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoadmapController {

    private final OnetService onetService;
    private final AuthService authService;

    @GetMapping("/search-occupations")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        return ResponseEntity.ok(Map.of("success", true, "occupations", onetService.searchOccupations(keyword)));
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody Map<String, String> req) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = authService.getUserByEmail(email);
            return ResponseEntity.ok(onetService.generateRoadmap(user, req.get("occupation"), req.get("onetCode")));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}