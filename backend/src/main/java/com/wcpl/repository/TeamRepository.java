package com.wcpl.repository;

import com.wcpl.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
}
