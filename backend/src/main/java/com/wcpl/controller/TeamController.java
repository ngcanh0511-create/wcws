package com.wcpl.controller;

import com.wcpl.entity.Team;
import com.wcpl.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamRepository.findAll());
    }
}
