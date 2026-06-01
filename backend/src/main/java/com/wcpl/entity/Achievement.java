package com.wcpl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievements")
@Getter @Setter @NoArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String icon;

    @Column(name = "badge_color", nullable = false)
    private String badgeColor;

    @Column(name = "condition_type", nullable = false)
    private String conditionType;

    @Column(name = "condition_value")
    private Integer conditionValue;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
