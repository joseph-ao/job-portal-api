package com.example.jobportal.job_portal_api.controller;

import com.example.jobportal.job_portal_api.dto.JwtResponse;
import com.example.jobportal.job_portal_api.dto.LoginRequest;
import com.example.jobportal.job_portal_api.dto.SignupRequest;
import com.example.jobportal.job_portal_api.model.Candidate;
import com.example.jobportal.job_portal_api.model.Recruiter;
import com.example.jobportal.job_portal_api.model.User;
import com.example.jobportal.job_portal_api.repository.CandidateRepository;
import com.example.jobportal.job_portal_api.repository.RecruiterRepository;
import com.example.jobportal.job_portal_api.repository.UserRepository;
import com.example.jobportal.job_portal_api.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final CandidateRepository candidateRepo;
    private final RecruiterRepository recruiterRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo,
                          CandidateRepository candidateRepo,
                          RecruiterRepository recruiterRepo,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.candidateRepo = candidateRepo;
        this.recruiterRepo = recruiterRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> signup(@Valid @RequestBody SignupRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new JwtResponse("Error: Username is already taken"));
        }

        String profileId;
        if ("CANDIDATE".equalsIgnoreCase(req.getRole())) {
            Candidate c = new Candidate();
            c.setFirstName(req.getFirstName());
            c.setLastName(req.getLastName());
            c.setEmail(req.getEmail());
            c.setPhone(req.getPhone());

            if (req.getResumeUrl() == null || req.getResumeUrl().isEmpty()) {
                c.setResumeUrl("http://example.com/default-resume");
            } else {
                c.setResumeUrl(req.getResumeUrl());
            }

            profileId = candidateRepo.save(c).getId();
        } else {
            Recruiter r = new Recruiter();
            r.setCompanyName(req.getCompanyName());
            r.setCompanyEmail(req.getEmail());
            r.setCompanyPhone(req.getPhone());
            profileId = recruiterRepo.save(r).getId();
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(req.getRole().toUpperCase());
        u.setProfileId(profileId);
        userRepo.save(u);

        String token = jwtUtil.generateToken(u.getUsername(), u.getRole());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest req) {
        User u = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Error: Invalid username or password"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new BadCredentialsException("Error: Invalid username or password");
        }

        String token = jwtUtil.generateToken(u.getUsername(), u.getRole());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
