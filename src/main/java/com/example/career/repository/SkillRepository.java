package com.example.career.repository;

import com.example.career.model.Skill;
import com.example.career.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUser(User user);
    List<Skill> findByUserAndFromResume(User user, boolean fromResume);
    void deleteByUser(User user);
}