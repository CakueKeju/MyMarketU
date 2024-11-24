package com.mymarketu.backend.controller;

import com.mymarketu.backend.model.User;
import com.mymarketu.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public Map<String, String> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            response.put("message", "Username already exists!");
        } else {
            userRepository.save(user);
            response.put("message", "User registered successfully!");
        }
        return response;
    }
}
