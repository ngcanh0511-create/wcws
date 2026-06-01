package com.wcpl.repository;

import com.wcpl.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findByExternalId(String externalId);

    List<Match> findByStatusOrderByMatchDateAsc(String status);

    @Query("SELECT m FROM Match m WHERE m.matchDate BETWEEN :start AND :end ORDER BY m.matchDate ASC")
    List<Match> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT m FROM Match m WHERE m.status = 'SCHEDULED' AND m.predictionLockedAt <= :now")
    List<Match> findMatchesToLock(@Param("now") LocalDateTime now);
}
