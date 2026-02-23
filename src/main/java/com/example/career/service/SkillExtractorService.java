package com.example.career.service;

import com.example.career.model.Skill;
import com.example.career.model.User;
import com.example.career.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillExtractorService {

    private final SkillRepository skillRepository;

    private static final Map<String, List<String>> SKILL_CATEGORIES = new HashMap<>() {{
        put("Programming", Arrays.asList("java", "python", "javascript", "typescript", "c++", "c#", "ruby", "go", "swift", "kotlin", "rust", "php", "scala", "r", "matlab", "dart"));
        put("Web", Arrays.asList("html", "css", "react", "angular", "vue", "node.js", "nodejs", "express", "django", "flask", "spring", "spring boot", "next.js", "tailwind", "bootstrap", "jquery", "rest", "graphql"));
        put("Database", Arrays.asList("mysql", "postgresql", "mongodb", "redis", "elasticsearch", "oracle", "sql server", "sqlite", "dynamodb", "firebase", "sql", "nosql"));
        put("Cloud", Arrays.asList("aws", "azure", "google cloud", "gcp", "docker", "kubernetes", "jenkins", "ci/cd", "terraform", "ansible", "linux", "nginx", "microservices", "serverless"));
        put("AI/ML", Arrays.asList("machine learning", "deep learning", "artificial intelligence", "tensorflow", "pytorch", "keras", "scikit-learn", "pandas", "numpy", "data analysis", "tableau", "power bi", "nlp", "computer vision", "big data", "hadoop", "spark"));
        put("Mobile", Arrays.asList("android", "ios", "react native", "flutter", "xamarin", "ionic", "swiftui"));
        put("Tools", Arrays.asList("git", "github", "gitlab", "jira", "agile", "scrum", "selenium", "jest", "junit", "postman", "swagger", "figma", "webpack", "maven", "gradle"));
        put("Soft Skills", Arrays.asList("leadership", "communication", "teamwork", "problem solving", "project management", "time management", "analytical thinking"));
    }};

    public String extractTextFromFile(String filePath, String fileName) throws IOException {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf")) return extractFromPdf(filePath);
        if (lower.endsWith(".docx")) return extractFromDocx(filePath);
        if (lower.endsWith(".txt")) return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
        throw new IOException("Unsupported file format");
    }

    private String extractFromPdf(String filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractFromDocx(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath); XWPFDocument doc = new XWPFDocument(fis)) {
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph para : doc.getParagraphs()) text.append(para.getText()).append("\n");
            return text.toString();
        }
    }

    public List<String> extractSkills(String text) {
        String lowerText = text.toLowerCase();
        Set<String> found = new LinkedHashSet<>();
        for (Map.Entry<String, List<String>> cat : SKILL_CATEGORIES.entrySet()) {
            for (String skill : cat.getValue()) {
                if (Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE).matcher(lowerText).find()) {
                    found.add(skill.substring(0, 1).toUpperCase() + skill.substring(1));
                }
            }
        }
        return new ArrayList<>(found);
    }

    public String getSkillCategory(String skillName) {
        String lower = skillName.toLowerCase();
        for (Map.Entry<String, List<String>> cat : SKILL_CATEGORIES.entrySet()) {
            if (cat.getValue().contains(lower)) return cat.getKey();
        }
        return "Other";
    }

    @Transactional
    public void saveSkillsForUser(User user, List<String> skills) {
        List<Skill> existing = skillRepository.findByUserAndFromResume(user, true);
        skillRepository.deleteAll(existing);
        for (String name : skills) {
            skillRepository.save(Skill.builder().user(user).skillName(name).category(getSkillCategory(name)).fromResume(true).build());
        }
    }

    public List<String> getUserSkills(User user) {
        return skillRepository.findByUser(user).stream().map(Skill::getSkillName).collect(Collectors.toList());
    }
}