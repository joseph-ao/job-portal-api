package com.example.jobportal.job_portal_api.controller;

import com.example.jobportal.job_portal_api.model.Job;
import com.example.jobportal.job_portal_api.model.Recruiter;
import com.example.jobportal.job_portal_api.repository.JobRepository;
import com.example.jobportal.job_portal_api.repository.RecruiterRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobRepository jobRepo;
    private final RecruiterRepository recRepo;

    @Autowired
    public JobController(JobRepository jobRepo,
                         RecruiterRepository recRepo) {
        this.jobRepo = jobRepo;
        this.recRepo = recRepo;
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@Valid @RequestBody Job job) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Recruiter me = recRepo.findByCompanyEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Only recruiters may post jobs"));

        job.setRecruiterId(me.getId());
        Job saved = jobRepo.save(job);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public Page<Job> listJobs(
            @RequestParam(value = "title", required = false) String title,
            Pageable pageable
    ) {
        if (title != null && !title.isBlank()) {
            return jobRepo.findByTitleContainingIgnoreCase(title, pageable);
        }
        return jobRepo.findAll(pageable);
    }


    @GetMapping("/{id}")
    public Job getJob(@PathVariable String id) {
        return jobRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "The job you're searching for isn't found"));
    }

    @PutMapping("/{id}")
    public Job updateJob(@PathVariable String id,
                         @Valid @RequestBody Job incoming) {
        Job existing = jobRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "The job you want to update isn't found"));

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Recruiter me = recRepo.findByCompanyEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Only recruiters may update jobs"));

        if (!existing.getRecruiterId().equals(me.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to edit this job");
        }

        existing.setTitle(incoming.getTitle());
        existing.setCompany(incoming.getCompany());
        existing.setSalary(incoming.getSalary());
        return jobRepo.save(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable String id) {
        Job existing = jobRepo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "The job you want to delete isn't found"));

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Recruiter me = recRepo.findByCompanyEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Only recruiters may delete jobs"));

        if (!existing.getRecruiterId().equals(me.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to delete this job");
        }

        jobRepo.delete(existing);
        return ResponseEntity.noContent().build();
    }
}
