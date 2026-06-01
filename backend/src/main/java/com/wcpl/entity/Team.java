package com.wcpl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teams")
@Getter @Setter @NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "flag_url")
    private String flagUrl;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "external_id")
    private String externalId;
}
