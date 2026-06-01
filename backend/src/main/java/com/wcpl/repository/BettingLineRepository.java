package com.wcpl.repository;

import com.wcpl.entity.BettingLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BettingLineRepository extends JpaRepository<BettingLine, Long> {
    List<BettingLine> findByMatchId(Long matchId);
}
