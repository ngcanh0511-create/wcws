package com.wcpl.controller;

import com.wcpl.dto.request.TournamentPredictionRequest;
import com.wcpl.dto.response.TournamentPredictionOptionsResponse;
import com.wcpl.dto.response.TournamentPredictionResponse;
import com.wcpl.entity.User;
import com.wcpl.service.TournamentPredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tournament-predictions")
@RequiredArgsConstructor
public class TournamentPredictionController {

    private final TournamentPredictionService tournamentPredictionService;

    @GetMapping("/options")
    public ResponseEntity<TournamentPredictionOptionsResponse> getOptions() {
        return ResponseEntity.ok(tournamentPredictionService.getOptions());
    }

    @GetMapping("/me")
    public ResponseEntity<List<TournamentPredictionResponse>> getMyPredictions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tournamentPredictionService.getMyPredictions(user.getId()));
    }

    @PostMapping
    public ResponseEntity<TournamentPredictionResponse> place(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TournamentPredictionRequest req) {
        return ResponseEntity.ok(tournamentPredictionService.place(user, req));
    }
}
