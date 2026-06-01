package com.wcpl.repository;

import com.wcpl.entity.TournamentPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentPredictionRepository extends JpaRepository<TournamentPrediction, Long> {
    List<TournamentPrediction> findByUserId(Long userId);
    Optional<TournamentPrediction> findByUserIdAndPredictionType(Long userId, String predictionType);
    List<TournamentPrediction> findByPredictionTypeAndStatus(String predictionType, String status);
}
