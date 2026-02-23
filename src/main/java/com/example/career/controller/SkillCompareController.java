package com.example.career.controller;

import com.example.career.model.User;
import com.example.career.service.AuthService;
import com.example.career.service.OnetService;
import com.example.career.service.SkillExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SkillCompareController {

    private final OnetService onetService;
    private final AuthService authService;
    private final SkillExtractorService skillExtractorService;

    @GetMapping("/my-skills")
    public ResponseEntity<?> mySkills() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return ResponseEntity.ok(Map.of("success", true, "skills", skillExtractorService.getUserSkills(authService.getUserByEmail(email))));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/compare")
    public ResponseEntity<?> compare(@RequestBody Map<String, String> req) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return ResponseEntity.ok(onetService.compareSkills(authService.getUserByEmail(email), req.get("occupation"), req.get("onetCode")));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}