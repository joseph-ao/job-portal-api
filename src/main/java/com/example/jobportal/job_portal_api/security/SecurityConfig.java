package com.example.jobportal.job_portal_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final MyUserDetailsService uds;
    private final JwtUtil jwtUtil;

    public SecurityConfig(MyUserDetailsService uds, JwtUtil jwtUtil) {
        this.uds = uds;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationConfiguration authConfig) throws Exception {
        JwtFilter jwtFilter = new JwtFilter(jwtUtil, uds);

        http
                // 1) No sessions or CSRF for a stateless JWT API
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2) Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // a) Public endpoints (signup/login)
                        .requestMatchers("/auth/**").permitAll()

                        // b) Public read-only (anyone can GET lists or single resources)
                        .requestMatchers(HttpMethod.GET,
                                "/jobs/**",
                                "/applications/**",
                                "/candidates/**",
                                "/recruiters/**"
                        ).permitAll()

                        // c) Job management: only recruiters can POST/PUT/DELETE
                        .requestMatchers(HttpMethod.POST,   "/jobs/**").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PUT,    "/jobs/**").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.DELETE, "/jobs/**").hasRole("RECRUITER")

                        // d) Application management: only candidates can POST/PUT/DELETE
                        .requestMatchers(HttpMethod.POST,   "/applications/**").hasRole("CANDIDATE")
                        .requestMatchers(HttpMethod.PUT,    "/applications/**").hasRole("CANDIDATE")
                        .requestMatchers(HttpMethod.DELETE, "/applications/**").hasRole("CANDIDATE")

                        // e) Candidate profile edits: only candidates can update/delete themselves
                        .requestMatchers(HttpMethod.PUT,    "/candidates/**").hasRole("CANDIDATE")
                        .requestMatchers(HttpMethod.DELETE, "/candidates/**").hasRole("CANDIDATE")

                        // f) Everything else (e.g. recruiter CRUD, other endpoints) requires a valid JWT
                        .anyRequest().authenticated()
                )

                // 3) Inject our JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
