package com.example.jobportal.job_portal_api.controller;

import com.example.jobportal.job_portal_api.model.Recruiter;
import com.example.jobportal.job_portal_api.repository.RecruiterRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/recruiters")
public class RecruiterController {
    private final RecruiterRepository repo;

    @Autowired
    public RecruiterController(RecruiterRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<Recruiter> createRecruiter(@Valid @RequestBody Recruiter recruiter) {
        Recruiter saved = repo.save(recruiter);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Recruiter> listRecruiters() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Recruiter getRecruiter(@PathVariable String id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "The recruiter you're searching for isn't found"));
    }

    @PutMapping("/{id}")
    public Recruiter updateRecruiter(@PathVariable String id,
                                     @Valid @RequestBody Recruiter incoming) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setFirstName(incoming.getFirstName());
                    existing.setLastName(incoming.getLastName());
                    existing.setCompanyEmail(incoming.getCompanyEmail());
                    existing.setCompanyName(incoming.getCompanyName());
                    existing.setCompanyEmail(incoming.getCompanyEmail());
                    existing.setCompanyPhone(incoming.getCompanyPhone());
                    return repo.save(existing);
                })
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "The recruiter you want to update isn't found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecruiter(@PathVariable String id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The recruiter you want to delete isn't found");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
