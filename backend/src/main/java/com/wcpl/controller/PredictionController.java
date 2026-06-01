package com.wcpl.controller;

import com.wcpl.dto.request.PlacePredictionRequest;
import com.wcpl.dto.response.PlacePredictionResponse;
import com.wcpl.entity.User;
import com.wcpl.service.PredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping
    public ResponseEntity<PlacePredictionResponse> place(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PlacePredictionRequest req) {
        return ResponseEntity.ok(predictionService.place(user, req));
    }
}
