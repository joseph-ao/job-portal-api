package com.example.jobportal.job_portal_api.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "applications")
public class Application {
    @Id
    private String id;
    @NotBlank(message = "Job ID is required")

    private String jobId;

    @NotBlank(message = "Candidate ID is required")
    private String candidateId;
    @NotBlank(message = "Status is required")
    private String status;
    private Instant appliedAt;

    public Application() {}

    public Application(String jobId, String candidateId) {
        this.jobId       = jobId;
        this.candidateId = candidateId;
        this.status      = "submitted";
        this.appliedAt   = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getJobId() {
        return jobId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAppliedAt(Instant appliedAt) {
        this.appliedAt = appliedAt;
    }
}
