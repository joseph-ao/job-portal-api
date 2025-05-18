package com.example.jobportal.job_portal_api.controller;

import com.example.jobportal.job_portal_api.dto.LoginRequest;
import com.example.jobportal.job_portal_api.dto.SignupRequest;
import com.example.jobportal.job_portal_api.model.User;
import com.example.jobportal.job_portal_api.repository.CandidateRepository;
import com.example.jobportal.job_portal_api.repository.RecruiterRepository;
import com.example.jobportal.job_portal_api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        // Clear data before each test
        userRepository.deleteAll();
        candidateRepository.deleteAll();
        recruiterRepository.deleteAll();
    }

    @AfterEach
    public void cleanup() {
        // Clear data after each test
        userRepository.deleteAll();
        candidateRepository.deleteAll();
        recruiterRepository.deleteAll();
    }

    @Test
    public void testCandidateSignup() throws Exception {
        // Create a sign-up request
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testcandidate");
        signupRequest.setPassword("password123");
        signupRequest.setRole("CANDIDATE");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPhone("123-456-7890");
        signupRequest.setResumeUrl("http://example.com/resume");

        // Execute request and validate response
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    public void testLogin() throws Exception {
        // First create a user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole("CANDIDATE");
        user.setProfileId("fakeid123");
        userRepository.save(user);

        // Create login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Execute login request and validate
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }
}