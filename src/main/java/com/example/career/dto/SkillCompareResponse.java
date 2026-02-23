package com.example.career.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class SkillCompareResponse {
    private String occupation;
    private List<String> userSkills;
    private List<String> requiredSkills;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> extraSkills;
    private double matchPercentage;
    private boolean success;
    private String message;
}