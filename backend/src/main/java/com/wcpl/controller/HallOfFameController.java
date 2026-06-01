package com.wcpl.controller;

import com.wcpl.entity.HallOfFame;
import com.wcpl.repository.HallOfFameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hall-of-fame")
@RequiredArgsConstructor
public class HallOfFameController {

    private final HallOfFameRepository hallOfFameRepository;

    @GetMapping
    public ResponseEntity<List<HallOfFame>> getAll() {
        return ResponseEntity.ok(hallOfFameRepository.findAll());
    }
}
