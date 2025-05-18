package com.example.jobportal.job_portal_api.repository;

import com.example.jobportal.job_portal_api.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobRepository extends MongoRepository<Job, String>{
    Page<Job> findAll(Pageable pageable);
    Page<Job> findByTitleContainingIgnoreCase(String title, Pageable pageable);


}
