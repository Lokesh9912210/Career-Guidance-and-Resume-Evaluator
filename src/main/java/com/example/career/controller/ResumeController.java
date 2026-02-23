package com.example.career.controller;

import com.example.career.model.Resume;
import com.example.career.model.User;
import com.example.career.service.AuthService;
import com.example.career.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeService resumeService;
    private final AuthService authService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = authService.getUserByEmail(email);
            if (file.isEmpty()) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Empty file"));
            return ResponseEntity.ok(resumeService.uploadAndProcessResume(file, user));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> latest() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Resume resume = resumeService.getLatestResume(authService.getUserByEmail(email));
            if (resume == null) return ResponseEntity.ok(Map.of("success", false, "message", "No resume"));
            return ResponseEntity.ok(Map.of("success", true, "resumeId", resume.getId(), "fileName", resume.getFileName(),
                    "skills", resume.getExtractedSkills() != null ? List.of(resume.getExtractedSkills().split(",")) : List.of()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Resume> resumes = resumeService.getAllResumes(authService.getUserByEmail(email));
            return ResponseEntity.ok(Map.of("success", true, "resumes", resumes.stream().map(r -> Map.of(
                    "id", r.getId(), "fileName", r.getFileName(), "uploadedAt", r.getUploadedAt().toString(),
                    "score", r.getResumeScore() != null ? r.getResumeScore() : 0)).collect(Collectors.toList())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}