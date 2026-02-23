package com.example.career.dto;

import lombok.Data;
import lombok.Builder;
import java.util.Map;
import java.util.List;

@Data
@Builder
public class ScoreResponse {
    private int overallScore;
    private Map<String, Integer> categoryScores;
    private List<String> strengths;
    private List<String> improvements;
    private String summary;
    private boolean success;
    private String message;
}