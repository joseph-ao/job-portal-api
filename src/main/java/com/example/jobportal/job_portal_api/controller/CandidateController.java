package com.example.jobportal.job_portal_api.controller;

import com.example.jobportal.job_portal_api.repository.CandidateRepository;
import com.example.jobportal.job_portal_api.model.Candidate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateRepository repo;

    @Autowired
    public CandidateController(CandidateRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}")
    public Candidate getCandidate(@PathVariable String id){
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "The candidate you're searching for isn't found"));
    }

    @PostMapping
    public ResponseEntity<Candidate> createCandidate(@Valid @RequestBody Candidate candidate){
        Candidate saved = repo.save(candidate);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public Candidate updateCandidate(@PathVariable String id, @Valid @RequestBody Candidate incoming){
        return repo.findById(id)
                .map(existing -> {//exisitng we chose candidate rn
                    existing.setFirstName(incoming.getFirstName());//incoming from method var, the one we got from requestbody
                    existing.setLastName(incoming.getLastName());
                    existing.setEmail( incoming.getEmail());
                    existing.setPhone(incoming.getPhone());
                    existing.setResumeUrl(incoming.getResumeUrl());
                    return repo.save(existing);
                })
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "The candidate you want to update isn't found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable String id){
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The candidate you want to delete isn't found");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Candidate> listAllCandidates() {
        return repo.findAll();
    }


}