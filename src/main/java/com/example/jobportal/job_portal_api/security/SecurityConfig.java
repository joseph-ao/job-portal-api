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
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/jobs/**",
                                "/applications/**",
                                "/candidates/**",
                                "/recruiters/**"
                        ).permitAll()

                       // recruiters can POST/PUT/DELETE
                        .requestMatchers(HttpMethod.POST,   "/jobs/**").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PUT,    "/jobs/**").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.DELETE, "/jobs/**").hasRole("RECRUITER")

                        // candidates can POST/PUT/DELETE
                        .requestMatchers(HttpMethod.POST,   "/applications/**").hasRole("CANDIDATE")
                        .requestMatchers(HttpMethod.PUT,    "/applications/**").hasRole("CANDIDATE")
                        .requestMatchers(HttpMethod.DELETE, "/applications/**").hasRole("CANDIDATE")

                        // candidates can update/delete themselves
                        .requestMatchers(HttpMethod.PUT,    "/candidates/**").hasRole("CANDIDATE")
                        .requestMatchers(HttpMethod.DELETE, "/candidates/**").hasRole("CANDIDATE")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
