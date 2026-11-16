
package com.ecotrack.repository;

import com.ecotrack.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentRepository
        extends JpaRepository<Incident, Long> {

    Optional<Incident> findByIncidentCode(String incidentCode);
}
