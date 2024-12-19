/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.UserDTO;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Cakue
 */
@Controller
public class AuthController {
    
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";  
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute @Valid UserDTO userDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("userDTO", userDTO);
            return "register";
        }

        // Konversi UserDTO ke User
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setNim(userDTO.getNim());
        user.setPassword(userDTO.getPassword());
        user.getRoles().add("USER"); // Default role

        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Username atau password salah.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Anda telah berhasil logout.");
        }
        return "login";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ADMIN")) {
                return "redirect:/admin/home";
            } else if (authority.getAuthority().equals("USER")) {
                return "redirect:/user/home";
            }
        }
        return "redirect:/login";
    }
}
