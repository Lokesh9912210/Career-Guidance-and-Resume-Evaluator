package com.example.career.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class ResumeResponse {
    private Long resumeId;
    private String fileName;
    private String extractedText;
    private List<String> extractedSkills;
    private String message;
    private boolean success;
}