
package com.ecotrack.controller;

import com.ecotrack.entity.Incident;
import com.ecotrack.enums.WasteCategory;
import com.ecotrack.service.IncidentService;
import com.ecotrack.service.ImageStorageService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "*")
public class IncidentController {

    private final IncidentService incidentService;
    private final ImageStorageService imageStorageService;

    public IncidentController(
            IncidentService incidentService,
            ImageStorageService imageStorageService) {

        this.incidentService = incidentService;
        this.imageStorageService = imageStorageService;
    }

    // Existing JSON endpoint
    @PostMapping
    public Incident registerIncident(@RequestBody Incident incident) {
        return incidentService.registerIncident(incident);
    }

    // New endpoint for incident with photograph
    @PostMapping(
        value = "/with-image",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Incident registerIncidentWithImage(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false)
                    String description,
            @RequestParam("wasteCategory") WasteCategory wasteCategory,
            @RequestParam("location") String location,
            @RequestParam(value = "latitude", required = false)
                    Double latitude,
            @RequestParam(value = "longitude", required = false)
                    Double longitude,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        // Store the uploaded photograph
        String imageName = imageStorageService.storeImage(image);

        // Create incident object
        Incident incident = new Incident();

        incident.setTitle(title);
        incident.setDescription(description);
        incident.setWasteCategory(wasteCategory);
        incident.setLocation(location);
        incident.setLatitude(latitude);
        incident.setLongitude(longitude);
        incident.setImagePath(imageName);

        // Save incident in MySQL
        return incidentService.registerIncident(incident);
    }

    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/{id}")
    public Incident getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id);
    }

    @GetMapping("/code/{code}")
    public Incident getIncidentByCode(@PathVariable String code) {
        return incidentService.getIncidentByCode(code);
    }
}

