package com.example.career.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String company;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String salaryMin;
    private String salaryMax;
    private String currency;
    private String jobUrl;
    private String category;
    private String contractType;
    private LocalDateTime fetchedAt;

    @PrePersist
    protected void onCreate() {
        fetchedAt = LocalDateTime.now();
    }
}