
package com.ecotrack.entity;

import com.ecotrack.enums.IncidentStatus;
import com.ecotrack.enums.WasteCategory;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String incidentCode;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WasteCategory wasteCategory;

    @Column(nullable = false)
    private String location;

    private Double latitude;

    private Double longitude;

    private String imagePath;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    private LocalDateTime reportedAt;

    public Incident() {
    }

    @PrePersist
    public void beforeSave() {
        this.incidentCode = "ECO-" +
                UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();

        this.reportedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = IncidentStatus.REPORTED;
        }
    }

    public Long getId() {
        return id;
    }

    public String getIncidentCode() {
        return incidentCode;
    }

    public void setIncidentCode(String incidentCode) {
        this.incidentCode = incidentCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WasteCategory getWasteCategory() {
        return wasteCategory;
    }

    public void setWasteCategory(WasteCategory wasteCategory) {
        this.wasteCategory = wasteCategory;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }
}
