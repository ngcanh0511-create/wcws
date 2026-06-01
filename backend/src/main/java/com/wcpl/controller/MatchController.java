package com.wcpl.controller;

import com.wcpl.dto.response.MatchResponse;
import com.wcpl.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAll() {
        return ResponseEntity.ok(matchService.getAll());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MatchResponse>> getUpcoming() {
        return ResponseEntity.ok(matchService.getUpcoming());
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<MatchResponse>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(matchService.getByDate(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getById(id));
    }
}
