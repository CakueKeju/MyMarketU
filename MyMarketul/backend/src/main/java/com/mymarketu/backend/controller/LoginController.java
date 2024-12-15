package com.mymarketu.backend.controller;

import com.mymarketu.backend.model.User;
import com.mymarketu.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, String> loginUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElse(null);

        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            response.put("message", "Login successful!");
        } else {
            response.put("message", "Invalid username or password!");
        }
        return response;
    }
}
