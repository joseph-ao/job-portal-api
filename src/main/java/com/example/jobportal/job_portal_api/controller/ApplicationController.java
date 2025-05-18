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

import java.util.List;

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

    // Create
    @PostMapping
    public ResponseEntity<Application> createApplication(
            @Valid @RequestBody Application app
    ) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Candidate me = candidateRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Only candidates may apply"));

        app.setCandidateId(me.getId());
        Application saved = appRepo.save(app);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Read All for this candidate
    @GetMapping
    public Page<Application> listApplications(
            @RequestParam(value = "status", required = false) String status,
            Pageable pageable
    ) {
        if (status != null && !status.isBlank()) {
            return appRepo.findByStatus(status, pageable);
        }
        // Or show only the caller’s applications:
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Candidate me = candidateRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Only candidates may view their applications"));        return appRepo.findByCandidateId(me.getId(), pageable);
    }

    // Read One
    @GetMapping("/{id}")
    public Application getApplication(@PathVariable String id) {
        Application existing = appRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Candidate me = candidateRepo.findByEmail(email).get();
        if (!existing.getCandidateId().equals(me.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to view this application");
        }
        return existing;
    }

    // Update
    @PutMapping("/{id}")
    public Application updateApplication(
            @PathVariable String id,
            @Valid @RequestBody Application incoming
    ) {
        Application existing = appRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Candidate me = candidateRepo.findByEmail(email).get();
        if (!existing.getCandidateId().equals(me.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to modify this application");
        }

        existing.setStatus(incoming.getStatus());
        return appRepo.save(existing);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable String id) {
        Application existing = appRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Candidate me = candidateRepo.findByEmail(email).get();
        if (!existing.getCandidateId().equals(me.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to withdraw this application");
        }

        appRepo.delete(existing);
        return ResponseEntity.noContent().build();
    }
}
