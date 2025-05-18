package com.example.jobportal.job_portal_api.repository;

import com.example.jobportal.job_portal_api.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ApplicationRepository extends MongoRepository<Application,String> {
    List<Application> findByCandidateId(String candidateId);// cz candidates can have multiple applications
    Page<Application> findByCandidateId(String candidateId, Pageable pageable);
    Page<Application> findAll(Pageable pageable);
    Page<Application> findByStatus(String status, Pageable pageable);                                           // and cz findBySomething does not come with MongoRepository to use in controller
}
