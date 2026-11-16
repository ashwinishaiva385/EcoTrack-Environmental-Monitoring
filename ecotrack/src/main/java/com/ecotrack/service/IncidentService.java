
package com.ecotrack.service;

import com.ecotrack.entity.Incident;
import com.ecotrack.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // Register a new waste incident
    public Incident registerIncident(Incident incident) {

        return incidentRepository.save(incident);
    }

    // Get all reported incidents
    public List<Incident> getAllIncidents() {

        return incidentRepository.findAll();
    }

    // Get incident by database ID
    public Incident getIncidentById(Long id) {

        return incidentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Incident not found with ID: " + id
                        ));
    }

    // Get incident using unique incident code
    public Incident getIncidentByCode(String code) {

        return incidentRepository.findByIncidentCode(code)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Incident not found: " + code
                        ));
    }
}
