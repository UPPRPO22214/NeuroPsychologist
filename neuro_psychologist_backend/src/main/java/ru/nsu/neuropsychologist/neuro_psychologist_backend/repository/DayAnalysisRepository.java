package ru.nsu.neuropsychologist.neuro_psychologist_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.entity.DayAnalysis;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.entity.User;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface DayAnalysisRepository extends JpaRepository<DayAnalysis, Long> {
    
    List<DayAnalysis> findByUserOrderByAnalyzedAtDesc(User user);
    
    List<DayAnalysis> findByUserAndAnalyzedAtBetweenOrderByAnalyzedAtDesc(
        User user,
        ZonedDateTime startDate,
        ZonedDateTime endDate
    );
    
    List<DayAnalysis> findTop10ByUserOrderByAnalyzedAtDesc(User user);
    
    Page<DayAnalysis> findByUserOrderByAnalyzedAtDesc(User user, Pageable pageable);
}