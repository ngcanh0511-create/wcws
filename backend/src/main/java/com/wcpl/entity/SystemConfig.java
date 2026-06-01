package com.wcpl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_config")
@Getter @Setter @NoArgsConstructor
public class SystemConfig {

    @Id
    private String key;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String description;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
