package com.cakwe.MyMarketU.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/customer")
public class CustomerSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSettingsController.class);

    @Autowired
    private UserService userService;

    @Value("${app.upload.dir:${user.home}}")
    private String uploadDir;

    @GetMapping("/profile-settings")
public String showProfileSettings(Model model) {
    try {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);
        model.addAttribute("userName", currentUser.getNamaLengkap());
        model.addAttribute("email", currentUser.getEmail());
        model.addAttribute("nim", currentUser.getNim()); // Pastikan atribut nim ditambahkan
        return "customer/profile-settings";
    } catch (Exception e) {
        model.addAttribute("errorMessage", "Gagal memuat data profil: " + e.getMessage());
        return "error";
    }
}

@PostMapping("/profile-settings/save")
public String saveProfileSettings(
        @RequestParam("userName") String userName,
        @RequestParam("email") String email,
        @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
        RedirectAttributes redirectAttributes) {
    try {
        User currentUser = userService.getCurrentUser(); // Ambil user yang login

        currentUser.setNamaLengkap(userName);
        currentUser.setEmail(email);

        // Proses foto profil jika ada file diunggah
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            String uploadDir = "src/main/resources/static/img/profile/";
            String fileName = System.currentTimeMillis() + "_" + profilePhoto.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Simpan file ke direktori
            Files.copy(profilePhoto.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            // Perbarui path foto profil ke user
            currentUser.setFotoProfil(fileName);
        }

        userService.updateProfile(currentUser); // Simpan perubahan user
        redirectAttributes.addFlashAttribute("successMessage", "Profil berhasil diperbarui!");

    } catch (IOException e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Gagal mengunggah foto profil: " + e.getMessage());
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan: " + e.getMessage());
    }
    return "redirect:/customer/profile-settings";
}


    @GetMapping("/security-settings")
    public String showSecuritySettings(Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            model.addAttribute("userName", currentUser.getNamaLengkap());
            model.addAttribute("user", currentUser);
            return "customer/security-settings";
        } catch (Exception e) {
            logger.error("Error pada showSecuritySettings: ", e);
            model.addAttribute("errorMessage", "Gagal memuat pengaturan keamanan: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/security-settings/save")
    public String saveSecuritySettings(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password saat ini tidak boleh kosong");
                return "redirect:/customer/security-settings";
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak boleh kosong");
                return "redirect:/customer/security-settings";
            }

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak cocok dengan konfirmasi password");
                return "redirect:/customer/security-settings";
            }

            if (newPassword.equals(currentPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak boleh sama dengan password saat ini");
                return "redirect:/customer/security-settings";
            }

            User currentUser = userService.getCurrentUser();
            String result = userService.updatePassword(currentUser.getId(), currentPassword, newPassword);

            if ("Password berhasil diperbarui".equals(result)) {
                redirectAttributes.addFlashAttribute("successMessage", result);
                logger.info("Password berhasil diperbarui untuk user: {}", currentUser.getEmail());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
                logger.warn("Gagal memperbarui password: {}", result);
            }
        } catch (Exception e) {
            logger.error("Error pada saveSecuritySettings: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui password: " + e.getMessage());
        }
        return "redirect:/customer/security-settings";
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        return email.matches(emailRegex);
    }
}
