/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Role;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
/**
 *
 * @author Cakue
 */
@Controller
public class RegisterController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/register")
    public String daftarPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/registration")
    public String registerUser(User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Invalid input data");
            return "register";
        }

        if (user.getRole() == null) {
            Role defaultRole = new Role(2L, "Customer"); // Role default untuk pengguna baru
            user.setRole(defaultRole);
        }
        
        String registrationStatus = userService.registerUser(user);

        if ("Registration successful".equals(registrationStatus)) {
            return "redirect:/login?registered=true"; // Redirect ke login setelah berhasil registrasi
        } else {
            model.addAttribute("errorMessage", registrationStatus); // Tampilkan pesan error
            return "register";
        }
    }

}