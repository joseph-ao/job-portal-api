package com.example.jobportal.job_portal_api.model;
import jakarta.validation.constraints.Min;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Document (collection= "jobs")

public class Job {
    @Id
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company is required")
    private String company;

    @Min(value = 0, message = "Salary must be non-negative")
    private Double salary;

    private String recruiterId;
    public Job(){
    }

    public Job(String title, String company, Double salary) {
        this.title = title;
        this.company = company;
        this.salary = salary;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public String getRecruiterId() {
        return recruiterId;
    }
    public void setRecruiterId(String recruiterId) {
        this.recruiterId = recruiterId;
    }
}