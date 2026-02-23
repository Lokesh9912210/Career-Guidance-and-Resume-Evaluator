package com.example.career.repository;

import com.example.career.model.JobListing;
import com.example.career.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobListingRepository extends JpaRepository<JobListing, Long> {
    List<JobListing> findByUser(User user);
}