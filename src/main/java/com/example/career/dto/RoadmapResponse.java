package com.example.career.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class RoadmapResponse {
    private String targetOccupation;
    private String onetCode;
    private List<String> requiredSkills;
    private List<String> userCurrentSkills;
    private List<String> missingSkills;
    private List<RoadmapStep> roadmapSteps;
    private boolean success;
    private String message;

    @Data
    @Builder
    public static class RoadmapStep {
        private int stepNumber;
        private String skillName;
        private String description;
        private String estimatedTime;
        private String difficulty;
        private List<String> resources;
    }
}