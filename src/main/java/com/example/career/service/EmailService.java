package com.example.career.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Async
    public void sendResultsEmail(String toEmail, String userName, int score,
                                  List<String> skills, List<String> improvements,
                                  String summary, int jobsFound) {
        try {
            // Email sending disabled - just log it
            log.info("========== EMAIL RESULTS ==========");
            log.info("To: {}", toEmail);
            log.info("User: {}", userName);
            log.info("Score: {}/100", score);
            log.info("Skills: {}", skills);
            log.info("Summary: {}", summary);
            log.info("Jobs Found: {}", jobsFound);
            log.info("===================================");
            log.info("Email sending skipped (no mail configured). Results logged above.");
        } catch (Exception e) {
            log.error("Email log failed: {}", e.getMessage());
        }
    }
}