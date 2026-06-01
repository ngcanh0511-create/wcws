package com.wcpl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_predictions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "prediction_type"}))
@Getter @Setter @NoArgsConstructor
public class TournamentPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "prediction_type", nullable = false)
    private String predictionType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "player_name")
    private String playerName;

    @Column(name = "credit_bet", nullable = false)
    private Integer creditBet;

    @Column(name = "credit_result")
    private Integer creditResult;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
