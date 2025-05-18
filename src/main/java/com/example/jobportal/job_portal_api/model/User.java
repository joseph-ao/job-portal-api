package com.example.jobportal.job_portal_api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="users")
public class User {
    @Id
    private String id;
    private String username;
    private String password;      // store the BCrypt hash
    private String role;          // e.g. "CANDIDATE" or "RECRUITER"
    private String profileId;     // the Mongo‐id of the Candidate or Recruiter

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
