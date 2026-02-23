package com.example.career.controller;

import com.example.career.dto.ScoreResponse;
import com.example.career.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScoreController {

    private final ResumeScoreService resumeScoreService;
    private final AuthService authService;
    private final EmailService emailService;
    private final MakeWebhookService makeWebhookService;
    private final SkillExtractorService skillExtractorService;

    @GetMapping("/resume")
    public ResponseEntity<?> score() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return ResponseEntity.ok(resumeScoreService.scoreResume(authService.getUserByEmail(email)));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/send-results")
    public ResponseEntity<?> sendResults() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = authService.getUserByEmail(email);
            ScoreResponse score = resumeScoreService.scoreResume(user);
            List<String> skills = skillExtractorService.getUserSkills(user);

            emailService.sendResultsEmail(user.getEmail(), user.getFullName(), score.getOverallScore(), skills, score.getImprovements(), score.getSummary(), 0);
            makeWebhookService.sendResultsToMake(user, score.getOverallScore(), skills, score.getImprovements(), 0);

            return ResponseEntity.ok(Map.of("success", true, "message", "Results sent to email and Telegram!"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}