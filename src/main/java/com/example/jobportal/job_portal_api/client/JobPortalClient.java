package com.example.jobportal.job_portal_api.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JobPortalClient {
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\nWelcome to the Job Portal CLI!");
            System.out.println("------------------------------");
            System.out.println("1. Candidate Signup");
            System.out.println("2. Candidate Login");
            System.out.println("3. Recruiter Signup");
            System.out.println("4. Recruiter Login");
            System.out.println("0. Exit");
            System.out.println("------------------------------");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    candidateSignup();
                    break;
                case 2:
                    candidateLogin();
                    break;
                case 3:
                    recruiterSignup();
                    break;
                case 4:
                    recruiterLogin();
                    break;
                case 0:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    private static void candidateSignup() {
        System.out.println("\n--- Candidate Signup ---");

        Map<String, Object> signupPayload = new HashMap<>();
        signupPayload.put("username", prompt("Username"));
        signupPayload.put("password", prompt("Password"));
        signupPayload.put("role", "CANDIDATE");
        signupPayload.put("firstName", prompt("First Name"));
        signupPayload.put("lastName", prompt("Last Name"));
        signupPayload.put("email", prompt("Email"));
        signupPayload.put("phone", prompt("Phone"));
        signupPayload.put("resumeUrl", prompt("Resume URL"));

        sendApiRequest("http://localhost:8080/auth/signup", signupPayload);
    }

    private static void candidateLogin() {
        System.out.println("\n--- Candidate Login ---");

        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("username", prompt("Username"));
        loginPayload.put("password", prompt("Password"));

        sendApiRequest("http://localhost:8080/auth/login", loginPayload);
    }

    private static void recruiterSignup() {
        System.out.println("\n--- Recruiter Signup ---");

        Map<String, Object> signupPayload = new HashMap<>();
        signupPayload.put("username", prompt("Username"));
        signupPayload.put("password", prompt("Password"));
        signupPayload.put("role", "RECRUITER");
        signupPayload.put("companyName", prompt("Company Name"));
        signupPayload.put("email", prompt("Company Email"));
        signupPayload.put("phone", prompt("Company Phone"));

        sendApiRequest("http://localhost:8080/auth/signup", signupPayload);
    }

    private static void recruiterLogin() {
        System.out.println("\n--- Recruiter Login ---");

        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("username", prompt("Username"));
        loginPayload.put("password", prompt("Password"));

        sendApiRequest("http://localhost:8080/auth/login", loginPayload);
    }

    private static String prompt(String field) {
        System.out.print(field + ": ");
        return scanner.nextLine();
    }

    private static void sendApiRequest(String url, Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Operation Successful! Response:");
                System.out.println(response.getBody());
            } else {
                System.out.println("Operation failed. Status Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("An error occurred while communicating with the API:");
            System.out.println(e.getMessage());
        }
    }
}
