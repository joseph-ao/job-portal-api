package com.example.jobportal.job_portal_api.controller;

import com.example.jobportal.job_portal_api.model.Application;
import com.example.jobportal.job_portal_api.model.Candidate;
import com.example.jobportal.job_portal_api.repository.ApplicationRepository;
import com.example.jobportal.job_portal_api.repository.CandidateRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationRepository appRepo;
    private final CandidateRepository candidateRepo;

    @Autowired
    public ApplicationController(ApplicationRepository appRepo,
                                 CandidateRepository candidateRepo) {
        this.appRepo = appRepo;
        this.candidateRepo = candidateRepo;
    }

    @PostMapping
    public ResponseEntity<Application> createApplication(
            @Valid @RequestBody Application app
    ) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        System.out.println("Creating application. Auth email: " + email);

        Candidate me = candidateRepo.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("No candidate found for email: " + email);
                    return new ResponseStatusException(
                            HttpStatus.FORBIDDEN, "Only candidates may apply");
                });

        System.out.println("Candidate found: " + me.getId());

        app.setCandidateId(me.getId());

        if (app.getStatus() == null || app.getStatus().isEmpty()) {
            app.setStatus("submitted");
        }

        if (app.getAppliedAt() == null) {
            app.setAppliedAt(Instant.now());
        }

        Application saved = appRepo.save(app);
        System.out.println("Application saved: " + saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public Page<Application> listApplications(
            @RequestParam(value = "status", required = false) String status,
            Pageable pageable
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Listing applications. Auth email: " + email);

        if (status != null && !status.isBlank()) {
            return appRepo.findByStatus(status, pageable);
        }

        Candidate me = candidateRepo.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("No candidate found for email: " + email);
                    return new ResponseStatusException(
                            HttpStatus.FORBIDDEN, "Only candidates may view their applications");
                });

        System.out.println("Candidate found: " + me.getId());
        return appRepo.findByCandidateId(me.getId(), pageable);
    }

}