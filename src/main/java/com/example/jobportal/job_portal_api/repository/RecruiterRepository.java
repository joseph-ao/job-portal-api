package com.example.jobportal.job_portal_api.repository;

import com.example.jobportal.job_portal_api.model.Recruiter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RecruiterRepository extends MongoRepository<Recruiter, String> {
    Optional<Recruiter> findByCompanyEmail(String companyEmail);
}
