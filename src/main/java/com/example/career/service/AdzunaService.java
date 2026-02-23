package com.example.career.service;

import com.example.career.dto.JobResponse;
import com.example.career.model.User;
import com.example.career.repository.JobListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdzunaService {

    @Value("${adzuna.api.base-url}")
    private String adzunaBaseUrl;

    @Value("${adzuna.api.app-id}")
    private String appId;

    @Value("${adzuna.api.app-key}")
    private String appKey;

    private final JobListingRepository jobListingRepository;
    private final SkillExtractorService skillExtractorService;

    public JobResponse searchJobs(User user, String country, String what, String where, int page) {
        try {
            if (what == null || what.isBlank()) {
                what = skillExtractorService.getUserSkills(user).stream().limit(5).collect(Collectors.joining(" "));
            }
            if (country == null || country.isBlank()) country = "us";

            String fw = what; String fc = country;
            Map response = WebClient.builder().baseUrl(adzunaBaseUrl).build().get()
                    .uri(u -> u.path("/jobs/" + fc + "/search/" + page)
                            .queryParam("app_id", appId).queryParam("app_key", appKey)
                            .queryParam("what", fw).queryParam("where", where != null ? where : "")
                            .queryParam("results_per_page", 20).build())
                    .retrieve().bodyToMono(Map.class).block();

            List<JobResponse.JobItem> jobs = new ArrayList<>();
            if (response != null && response.containsKey("results")) {
                for (Map<String, Object> r : (List<Map<String, Object>>) response.get("results")) {
                    Map<String, Object> comp = (Map<String, Object>) r.getOrDefault("company", Map.of());
                    Map<String, Object> loc = (Map<String, Object>) r.getOrDefault("location", Map.of());
                    Map<String, Object> cat = (Map<String, Object>) r.getOrDefault("category", Map.of());

                    jobs.add(JobResponse.JobItem.builder()
                            .title((String) r.getOrDefault("title", "N/A"))
                            .company((String) comp.getOrDefault("display_name", "N/A"))
                            .location((String) loc.getOrDefault("display_name", "Remote"))
                            .description((String) r.getOrDefault("description", ""))
                            .salaryMin(r.get("salary_min") != null ? r.get("salary_min").toString() : "N/A")
                            .salaryMax(r.get("salary_max") != null ? r.get("salary_max").toString() : "N/A")
                            .currency("USD").jobUrl((String) r.getOrDefault("redirect_url", ""))
                            .category((String) cat.getOrDefault("label", ""))
                            .contractType((String) r.getOrDefault("contract_type", ""))
                            .created((String) r.getOrDefault("created", "")).build());
                }
                int total = response.get("count") != null ? ((Number) response.get("count")).intValue() : jobs.size();
                return JobResponse.builder().jobs(jobs).totalResults(total).success(true).message("Found " + total + " jobs").build();
            }
            return JobResponse.builder().jobs(List.of()).totalResults(0).success(true).message("No jobs found").build();
        } catch (Exception e) {
            log.error("Adzuna error: {}", e.getMessage());
            return JobResponse.builder().jobs(getSampleJobs()).totalResults(2).success(true).message("Sample results").build();
        }
    }

    private List<JobResponse.JobItem> getSampleJobs() {
        return Arrays.asList(
            JobResponse.JobItem.builder().title("Software Developer").company("TechCorp").location("Remote")
                .description("Looking for developers").salaryMin("70000").salaryMax("120000").currency("USD")
                .jobUrl("https://example.com").category("IT").contractType("Full-time").build(),
            JobResponse.JobItem.builder().title("Data Analyst Intern").company("DataCo").location("New York")
                .description("Great internship").salaryMin("30000").salaryMax("45000").currency("USD")
                .jobUrl("https://example.com").category("IT").contractType("Internship").build()
        );
    }
}