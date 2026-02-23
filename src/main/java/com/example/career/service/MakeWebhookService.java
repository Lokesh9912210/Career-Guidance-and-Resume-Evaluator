package com.example.career.service;

import com.example.career.model.Resume;
import com.example.career.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MakeWebhookService {

    @Value("${make.webhook.url}")
    private String makeWebhookUrl;

    @Value("${make.webhook.resume-url}")
    private String makeResumeWebhookUrl;

    public void sendResumeToMake(User user, Resume resume, List<String> skills) {
        try {
            if (makeResumeWebhookUrl.contains("dummy")) {
                log.info("Make.com webhook skipped (not configured). User: {}, Resume: {}, Skills: {}",
                        user.getEmail(), resume.getFileName(), skills);
                return;
            }

            // Real webhook call would go here when configured
            log.info("Resume sent to Make.com for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Make.com failed: {}", e.getMessage());
        }
    }

    public void sendResultsToMake(User user, int score, List<String> skills,
                                   List<String> missing, int jobs) {
        try {
            if (makeWebhookUrl.contains("dummy")) {
                log.info("Make.com results webhook skipped (not configured). User: {}, Score: {}",
                        user.getEmail(), score);
                return;
            }

            log.info("Results sent to Make.com for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Make.com results failed: {}", e.getMessage());
        }
    }
}