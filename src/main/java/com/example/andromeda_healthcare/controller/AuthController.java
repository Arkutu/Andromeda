package com.example.andromeda_healthcare.controller;

import com.example.andromeda_healthcare.dto.LoginRequest;
import com.example.andromeda_healthcare.dto.RegistrationRequest;
import com.example.andromeda_healthcare.model.User;
import com.example.andromeda_healthcare.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
        logger.debug("Received registration request for email: {}", request.getEmail());
        try {
            User user = userService.registerUser(request);
            logger.info("User registered successfully with ID: {}", user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            response.put("userId", user.getId());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error during user registration", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        logger.debug("Received login request for email: {}", request.getEmail());
        try {
            User user = userService.loginUser(request);
            logger.info("User logged in successfully with ID: {}", user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("userId", user.getId());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error during user login", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}