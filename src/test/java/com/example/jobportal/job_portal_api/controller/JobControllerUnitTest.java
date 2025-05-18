package com.example.jobportal.job_portal_api.controller;

import com.example.jobportal.job_portal_api.model.Job;
import com.example.jobportal.job_portal_api.repository.JobRepository;
import com.example.jobportal.job_portal_api.repository.RecruiterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JobControllerUnitTest {

    @Autowired
    private JobController jobController;

    @MockBean
    private JobRepository jobRepository;

    @MockBean
    private RecruiterRepository recruiterRepository;

    @Test
    public void testGetJob() {
        // Arrange - create test data
        Job mockJob = new Job("Software Engineer", "Tech Company", 80000.0);
        mockJob.setId("test123");

        // Mock repository behavior
        when(jobRepository.findById("test123")).thenReturn(Optional.of(mockJob));

        // Act - call the method we want to test
        Job result = jobController.getJob("test123");

        // Assert - verify the result
        assertNotNull(result);
        assertEquals("test123", result.getId());
        assertEquals("Software Engineer", result.getTitle());
        assertEquals("Tech Company", result.getCompany());
        assertEquals(80000.0, result.getSalary());
    }

    @Test
    public void testListJobs() {
        // Arrange
        List<Job> mockJobs = Arrays.asList(
                new Job("Software Engineer", "Tech Company", 80000.0),
                new Job("Web Developer", "Web Company", 75000.0)
        );
        Page<Job> mockPage = new PageImpl<>(mockJobs);

        when(jobRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        // Act
        Page<Job> result = jobController.listJobs(null, Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Software Engineer", result.getContent().get(0).getTitle());
        assertEquals("Web Developer", result.getContent().get(1).getTitle());
    }
}