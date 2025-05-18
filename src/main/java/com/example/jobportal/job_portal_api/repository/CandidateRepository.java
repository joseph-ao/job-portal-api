package com.example.jobportal.job_portal_api.repository;

import com.example.jobportal.job_portal_api.model.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CandidateRepository extends MongoRepository<Candidate, String> {
    Optional<Candidate> findByEmail(String email);

}
