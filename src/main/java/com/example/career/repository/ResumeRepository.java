package com.example.career.repository;

import com.example.career.model.Resume;
import com.example.career.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUser(User user);
    Optional<Resume> findTopByUserOrderByUploadedAtDesc(User user);
}