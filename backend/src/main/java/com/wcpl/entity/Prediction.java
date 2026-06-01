package com.wcpl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
@Getter @Setter @NoArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "betting_line_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private BettingLine bettingLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "credit_bet", nullable = false)
    private Integer creditBet;

    @Column(name = "credit_result")
    private Integer creditResult;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
