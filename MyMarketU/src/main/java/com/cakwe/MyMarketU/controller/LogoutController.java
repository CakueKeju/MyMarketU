/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 *
 * @author Cakue
 */
@Controller
@RequestMapping("/logout")
public class LogoutController {

    @GetMapping
    public String logout(HttpSession session) {
        // Hapus semua atribut dari session
        session.invalidate();
        
        // Redirect ke halaman login setelah logout
        return "redirect:/login";
    }
}