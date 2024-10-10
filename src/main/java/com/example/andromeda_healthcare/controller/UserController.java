package com.example.andromeda_healthcare.controller;

import com.example.andromeda_healthcare.dto.RegistrationRequest;
import com.example.andromeda_healthcare.model.User;
import com.example.andromeda_healthcare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        User registeredUser = userService.registerUser(registrationRequest);
        return ResponseEntity.ok("User registered successfully with ID: " + registeredUser.getId());
    }
}
