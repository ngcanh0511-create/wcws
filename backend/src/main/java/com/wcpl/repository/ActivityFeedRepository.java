package com.wcpl.repository;

import com.wcpl.entity.ActivityFeed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Long> {

    @Query("SELECT a FROM ActivityFeed a ORDER BY a.createdAt DESC")
    List<ActivityFeed> findRecentGlobal(Pageable pageable);
}
