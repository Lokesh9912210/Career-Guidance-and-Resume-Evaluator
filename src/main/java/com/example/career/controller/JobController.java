package com.example.career.controller;

import com.example.career.service.AdzunaService;
import com.example.career.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobController {

    private final AdzunaService adzunaService;
    private final AuthService authService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String country, @RequestParam(required = false) String what,
                                     @RequestParam(required = false) String where, @RequestParam(defaultValue = "1") int page) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return ResponseEntity.ok(adzunaService.searchJobs(authService.getUserByEmail(email), country, what, where, page));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/skill-based")
    public ResponseEntity<?> skillBased(@RequestParam(defaultValue = "us") String country, @RequestParam(defaultValue = "1") int page) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return ResponseEntity.ok(adzunaService.searchJobs(authService.getUserByEmail(email), country, null, null, page));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}