package com.example.career.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class JobResponse {
    private List<JobItem> jobs;
    private int totalResults;
    private boolean success;
    private String message;

    @Data
    @Builder
    public static class JobItem {
        private String title;
        private String company;
        private String location;
        private String description;
        private String salaryMin;
        private String salaryMax;
        private String currency;
        private String jobUrl;
        private String category;
        private String contractType;
        private String created;
    }
}