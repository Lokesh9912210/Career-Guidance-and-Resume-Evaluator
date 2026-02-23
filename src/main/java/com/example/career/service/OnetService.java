package com.example.career.service;

import com.example.career.dto.RoadmapResponse;
import com.example.career.dto.SkillCompareResponse;
import com.example.career.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnetService {

    @Value("${onet.api.base-url}")
    private String onetBaseUrl;

    @Value("${onet.api.username}")
    private String onetUsername;

    @Value("${onet.api.password}")
    private String onetPassword;

    private final SkillExtractorService skillExtractorService;

    public List<Map<String, String>> searchOccupations(String keyword) {
        try {
            WebClient client = WebClient.builder().baseUrl(onetBaseUrl)
                    .defaultHeaders(h -> { h.setBasicAuth(onetUsername, onetPassword); h.setAccept(List.of(MediaType.APPLICATION_JSON)); }).build();

            Map response = client.get().uri(u -> u.path("/online/search").queryParam("keyword", keyword).queryParam("end", 10).build())
                    .retrieve().bodyToMono(Map.class).block();

            List<Map<String, String>> occupations = new ArrayList<>();
            if (response != null && response.containsKey("occupation")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("occupation");
                for (Map<String, Object> occ : list) {
                    occupations.add(Map.of("code", (String) occ.get("code"), "title", (String) occ.get("title")));
                }
            }
            return occupations;
        } catch (Exception e) {
            log.error("O*NET error: {}", e.getMessage());
            return getDefaultOccupations();
        }
    }

    public List<String> getOccupationSkills(String onetCode) {
        try {
            WebClient client = WebClient.builder().baseUrl(onetBaseUrl)
                    .defaultHeaders(h -> { h.setBasicAuth(onetUsername, onetPassword); h.setAccept(List.of(MediaType.APPLICATION_JSON)); }).build();

            Set<String> skills = new LinkedHashSet<>();
            Map tech = client.get().uri("/online/occupations/" + onetCode + "/summary/technology_skills")
                    .retrieve().bodyToMono(Map.class).block();

            if (tech != null && tech.containsKey("category")) {
                for (Map<String, Object> cat : (List<Map<String, Object>>) tech.get("category")) {
                    if (cat.containsKey("example")) {
                        for (Map<String, Object> ex : (List<Map<String, Object>>) cat.get("example"))
                            skills.add((String) ex.get("name"));
                    }
                }
            }
            return new ArrayList<>(skills);
        } catch (Exception e) {
            log.error("O*NET skills error: {}", e.getMessage());
            return getDefaultSkills(onetCode);
        }
    }

    public RoadmapResponse generateRoadmap(User user, String occupation, String onetCode) {
        List<String> userSkills = skillExtractorService.getUserSkills(user);
        List<String> required = getOccupationSkills(onetCode);
        List<String> userLower = userSkills.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<String> missing = required.stream().filter(s -> !userLower.contains(s.toLowerCase())).collect(Collectors.toList());

        List<RoadmapResponse.RoadmapStep> steps = new ArrayList<>();
        int n = 1;
        for (String skill : missing) {
            steps.add(RoadmapResponse.RoadmapStep.builder().stepNumber(n++).skillName(skill)
                    .description("Learn " + skill + " for " + occupation).estimatedTime("2-4 weeks").difficulty("Intermediate")
                    .resources(Arrays.asList("Coursera - " + skill, "Udemy - " + skill, "YouTube - " + skill)).build());
        }

        return RoadmapResponse.builder().targetOccupation(occupation).onetCode(onetCode)
                .requiredSkills(required).userCurrentSkills(userSkills).missingSkills(missing)
                .roadmapSteps(steps).success(true).message("Roadmap generated").build();
    }

    public SkillCompareResponse compareSkills(User user, String occupation, String onetCode) {
        List<String> userSkills = skillExtractorService.getUserSkills(user);
        List<String> required = getOccupationSkills(onetCode);
        List<String> userLower = userSkills.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<String> reqLower = required.stream().map(String::toLowerCase).collect(Collectors.toList());

        List<String> matched = userSkills.stream().filter(s -> reqLower.contains(s.toLowerCase())).collect(Collectors.toList());
        List<String> missing = required.stream().filter(s -> !userLower.contains(s.toLowerCase())).collect(Collectors.toList());
        List<String> extra = userSkills.stream().filter(s -> !reqLower.contains(s.toLowerCase())).collect(Collectors.toList());
        double pct = required.isEmpty() ? 0 : (double) matched.size() / required.size() * 100;

        return SkillCompareResponse.builder().occupation(occupation).userSkills(userSkills).requiredSkills(required)
                .matchedSkills(matched).missingSkills(missing).extraSkills(extra)
                .matchPercentage(Math.round(pct * 10.0) / 10.0).success(true).message("Compared").build();
    }

    private List<Map<String, String>> getDefaultOccupations() {
        return Arrays.asList(
            Map.of("code", "15-1252.00", "title", "Software Developers"),
            Map.of("code", "15-2051.00", "title", "Data Scientists"),
            Map.of("code", "15-1254.00", "title", "Web Developers"),
            Map.of("code", "15-1244.00", "title", "Network Engineers"),
            Map.of("code", "15-1212.00", "title", "Information Security Analysts")
        );
    }

    private List<String> getDefaultSkills(String code) {
        Map<String, List<String>> m = Map.of(
            "15-1252.00", Arrays.asList("Java", "Python", "JavaScript", "SQL", "Git", "Spring Boot", "REST APIs", "Docker", "AWS", "Agile"),
            "15-2051.00", Arrays.asList("Python", "R", "SQL", "Machine Learning", "TensorFlow", "Pandas", "NumPy", "Tableau", "Statistics"),
            "15-1254.00", Arrays.asList("HTML", "CSS", "JavaScript", "React", "Node.js", "Git", "MongoDB", "REST APIs", "TypeScript")
        );
        return m.getOrDefault(code, Arrays.asList("Programming", "Problem Solving", "Communication", "Teamwork"));
    }
}