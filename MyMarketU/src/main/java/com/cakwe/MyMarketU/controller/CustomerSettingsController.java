package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerSettingsController {

    @Autowired
    private UserService userService;

    // Menampilkan halaman pengaturan profil
    @GetMapping("/profile-settings")
    public String showProfileSettings(Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            model.addAttribute("userName", currentUser.getNamaLengkap());
            model.addAttribute("email", currentUser.getEmail());
            model.addAttribute("nim", currentUser.getNim());
            return "customer/profile-settings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Gagal memuat profil");
            return "error";
        }
    }

    // Menyimpan perubahan profil
    @PostMapping("/profile-settings/save")
    public String saveProfileSettings(
            @RequestParam("userName") String userName,
            @RequestParam("email") String email,
            @RequestParam("nim") String nim,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getCurrentUser();
            currentUser.setNamaLengkap(userName);
            currentUser.setEmail(email);
            currentUser.setNim(nim);

            String result = userService.updateProfile(currentUser);

            if (result.equals("Profil berhasil diperbarui")) {
                redirectAttributes.addFlashAttribute("successMessage", result);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui profil");
        }
        return "redirect:/customer/profile-settings";
    }

    // Menampilkan halaman pengaturan keamanan
    @GetMapping("/security-settings")
    public String showSecuritySettings(Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            model.addAttribute("userName", currentUser.getNamaLengkap());
            return "customer/security-settings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Gagal memuat pengaturan keamanan");
            return "error";
        }
    }

    // Menyimpan perubahan password
    @PostMapping("/security-settings/save")
public String updatePassword(@RequestParam("currentPassword") String currentPassword,
                              @RequestParam("newPassword") String newPassword,
                              @RequestParam("confirmPassword") String confirmPassword,
                              RedirectAttributes redirectAttributes) {
    try {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak cocok!");
            return "redirect:/customer/security-settings";
        }

        User currentUser = userService.getCurrentUser();
        String result = userService.updatePassword(currentUser.getId(), currentPassword, newPassword);

        if ("Password berhasil diperbarui".equals(result)) {
            redirectAttributes.addFlashAttribute("successMessage", result);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", result);
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui password: " + e.getMessage());
    }
    return "redirect:/customer/security-settings";
}

}
