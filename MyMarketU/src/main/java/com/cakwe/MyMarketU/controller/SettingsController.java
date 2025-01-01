package com.cakwe.MyMarketU.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.service.UserService;

@Controller
@RequestMapping("/admin")
public class SettingsController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile-settings-admin")
    public String showProfileSettings(Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            model.addAttribute("userName", currentUser.getNamaLengkap());
            model.addAttribute("email", currentUser.getEmail());
            return "admin/profile-settings-admin";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Gagal memuat profil");
            return "error";
        }
    }
    
    @PostMapping("/profile-settings-admin/save")
    public String saveProfileSettings(
            @RequestParam("userName") String userName,
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getCurrentUser();
            currentUser.setNamaLengkap(userName);
            currentUser.setEmail(email);
            
            String result = userService.updateProfile(currentUser);
            
            if(result.equals("Profil berhasil diperbarui")) {
                redirectAttributes.addFlashAttribute("successMessage", result);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui profil");
        }
        return "redirect:/admin/profile-settings-admin";
    }

    @GetMapping("/security-settings-admin")
    public String showSecuritySettings(Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            model.addAttribute("userName", currentUser.getNamaLengkap());
            return "admin/security-settings-admin";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Gagal memuat pengaturan keamanan");
            return "error";
        }
    }
    
    @PostMapping("/security-settings-admin/save")
    public String saveSecuritySettings(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak cocok!");
                return "redirect:/admin/security-settings-admin";
            }

            User currentUser = userService.getCurrentUser();
            String result = userService.updatePassword(currentUser.getId(), currentPassword, newPassword);
            
            if(result.equals("Password berhasil diperbarui")) {
                redirectAttributes.addFlashAttribute("successMessage", result);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui password");
        }
        return "redirect:/admin/security-settings-admin";
    }
}