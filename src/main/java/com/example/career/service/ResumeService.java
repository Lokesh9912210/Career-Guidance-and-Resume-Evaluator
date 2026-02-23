package com.example.career.service;

import com.example.career.dto.ResumeResponse;
import com.example.career.model.Resume;
import com.example.career.model.User;
import com.example.career.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final SkillExtractorService skillExtractorService;
    private final MakeWebhookService makeWebhookService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ResumeResponse uploadAndProcessResume(MultipartFile file, User user) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String original = file.getOriginalFilename();
        String unique = UUID.randomUUID() + "_" + original;
        Path filePath = uploadPath.resolve(unique);
        Files.copy(file.getInputStream(), filePath);

        String text = skillExtractorService.extractTextFromFile(filePath.toString(), original);
        List<String> skills = skillExtractorService.extractSkills(text);

        Resume resume = Resume.builder().user(user).fileName(original).filePath(filePath.toString())
                .extractedText(text).extractedSkills(String.join(",", skills)).build();
        resumeRepository.save(resume);

        skillExtractorService.saveSkillsForUser(user, skills);
        makeWebhookService.sendResumeToMake(user, resume, skills);

        return ResumeResponse.builder().resumeId(resume.getId()).fileName(original)
                .extractedText(text.substring(0, Math.min(500, text.length())) + "...")
                .extractedSkills(skills).success(true).message("Resume processed successfully").build();
    }

    public Resume getLatestResume(User user) {
        return resumeRepository.findTopByUserOrderByUploadedAtDesc(user).orElse(null);
    }

    public List<Resume> getAllResumes(User user) {
        return resumeRepository.findByUser(user);
    }
}