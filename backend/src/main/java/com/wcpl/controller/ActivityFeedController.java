package com.wcpl.controller;

import com.wcpl.entity.ActivityFeed;
import com.wcpl.repository.ActivityFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity-feed")
@RequiredArgsConstructor
public class ActivityFeedController {

    private final ActivityFeedRepository activityFeedRepository;

    @GetMapping
    public ResponseEntity<List<ActivityFeed>> getRecent(
            @RequestParam(defaultValue = "20") int limit) {
        int safeLimit = Math.min(limit, 50);
        return ResponseEntity.ok(activityFeedRepository.findRecentGlobal(PageRequest.of(0, safeLimit)));
    }
}
