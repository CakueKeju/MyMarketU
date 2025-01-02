/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 *
 * @author Cakue
 */
@Controller
public class LoginController {
    

    @Autowired
    private UserService userService;
     
    /**
    * Show login page.
    */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Email atau password salah");
        }
        return "login"; // Menampilkan halaman login.html
    }
    
    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes, 
            Model model) {

        User user = userService.authenticate(email, password);
        if (user != null) {
            String role = user.getRole().getNama();
            if ("Admin".equalsIgnoreCase(role)) {
                return "redirect:/admin/homepage";
            } else if ("Customer".equalsIgnoreCase(role)) {
                session.setAttribute("userId", user.getId());
                return "redirect:/customer/homepage";
            } else {
                model.addAttribute("errorMessage", "Invalid role.");
                return "login";
            }
        } else {
            model.addAttribute("errorMessage", "Email atau password salah.");
            return "redirect:/login";
        }
    }
}
