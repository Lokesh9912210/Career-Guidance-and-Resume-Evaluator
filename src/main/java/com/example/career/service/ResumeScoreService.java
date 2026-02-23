package com.example.career.service;

import com.example.career.dto.ScoreResponse;
import com.example.career.model.Resume;
import com.example.career.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ResumeScoreService {

    private final SkillExtractorService skillExtractorService;
    private final ResumeService resumeService;

    public ScoreResponse scoreResume(User user) {
        Resume resume = resumeService.getLatestResume(user);
        if (resume == null) return ScoreResponse.builder().overallScore(0).success(false).message("No resume found").build();

        String text = resume.getExtractedText();
        String lower = text.toLowerCase();
        List<String> skills = skillExtractorService.extractSkills(text);

        Map<String, Integer> scores = new LinkedHashMap<>();
        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();

        int skillScore = Math.min(25, skills.size() * 3);
        scores.put("Technical Skills", skillScore);
        if (skills.size() >= 8) strengths.add("Strong skills: " + skills.size() + " found"); else improvements.add("Add more skills");

        int exp = 0;
        if (lower.contains("experience")) exp += 5;
        Matcher ym = Pattern.compile("20\\d{2}").matcher(text); int yc = 0; while (ym.find()) yc++;
        exp += Math.min(5, yc);
        if (lower.contains("developer") || lower.contains("engineer")) exp += 5;
        if (Pattern.compile("\\d+%").matcher(text).find()) exp += 5;
        scores.put("Experience", Math.min(20, exp));
        if (exp >= 15) strengths.add("Detailed experience"); else improvements.add("Add quantifiable achievements");

        int edu = 0;
        if (lower.contains("education")) edu += 3;
        if (lower.contains("bachelor") || lower.contains("b.tech")) edu += 5;
        if (lower.contains("master") || lower.contains("mba")) edu += 5;
        if (lower.contains("university") || lower.contains("college")) edu += 2;
        scores.put("Education", Math.min(15, edu));

        int contact = 0;
        if (Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matcher(text).find()) contact += 3;
        if (lower.contains("linkedin")) contact += 3;
        if (lower.contains("github")) contact += 2;
        if (Pattern.compile("\\+?[\\d\\s-]{10,}").matcher(text).find()) contact += 2;
        scores.put("Contact Info", Math.min(10, contact));

        int fmt = 0;
        int words = text.split("\\s+").length;
        if (words >= 200 && words <= 1000) fmt += 5; else if (words >= 100) fmt += 3;
        if (text.contains("•") || text.contains("-")) fmt += 3;
        scores.put("Formatting", Math.min(10, fmt));

        String[] verbs = {"developed", "managed", "implemented", "designed", "created", "built", "led", "deployed"};
        int vc = 0; for (String v : verbs) if (lower.contains(v)) vc++;
        scores.put("Keywords", Math.min(10, vc * 2));
        if (vc >= 4) strengths.add("Good action verbs"); else improvements.add("Use verbs: developed, implemented, managed");

        int overall = scores.values().stream().mapToInt(Integer::intValue).sum();
        String summary = overall >= 80 ? "Excellent!" : overall >= 60 ? "Good with room to improve." : overall >= 40 ? "Average. Needs work." : "Needs significant improvement.";

        resume.setResumeScore(overall);

        return ScoreResponse.builder().overallScore(overall).categoryScores(scores)
                .strengths(strengths).improvements(improvements).summary(summary).success(true).message("Scored").build();
    }
}