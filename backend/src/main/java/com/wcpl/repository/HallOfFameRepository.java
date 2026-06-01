package com.wcpl.repository;

import com.wcpl.entity.HallOfFame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HallOfFameRepository extends JpaRepository<HallOfFame, Long> {
    List<HallOfFame> findBySeasonOrderByFinalRankAsc(String season);
}
