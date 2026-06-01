package com.wcpl.repository;

import com.wcpl.entity.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    Page<Prediction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Prediction> findByMatchIdAndStatus(Long matchId, String status);

    @Query("SELECT COUNT(p) FROM Prediction p WHERE p.match.id = :matchId AND p.bettingLine.id = :lineId")
    long countByMatchAndLine(@Param("matchId") Long matchId, @Param("lineId") Long lineId);

    @Query("SELECT p FROM Prediction p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Prediction> findRecentByUser(@Param("userId") Long userId, Pageable pageable);
}
