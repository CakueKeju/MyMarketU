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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class SettingsController {
    
    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);
    
    @Autowired
    private UserService userService;
    
    @Value("${app.upload.dir:${user.home}}")
    private String uploadDir;
    
    @GetMapping("/profile-settings-admin")
    public String showProfileSettings(Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            model.addAttribute("user", currentUser); // Menambahkan objek user lengkap untuk foto
            model.addAttribute("userName", currentUser.getNamaLengkap());
            model.addAttribute("email", currentUser.getEmail());
            return "admin/profile-settings-admin";
        } catch (Exception e) {
            logger.error("Error pada showProfileSettings: ", e);
            model.addAttribute("errorMessage", "Gagal memuat profil: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/profile-settings-admin/save")
    public String saveProfileSettings(
            @RequestParam("userName") String userName,
            @RequestParam("email") String email,
            @RequestParam(value = "fotoProfil", required = false) MultipartFile fotoProfil,
            RedirectAttributes redirectAttributes) {
        try {
            logger.info("Mencoba menyimpan profil dengan email: {}", email);
            
            // Validasi input
            if (userName == null || userName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Nama tidak boleh kosong");
                return "redirect:/admin/profile-settings-admin";
            }

            if (!isValidEmail(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Format email tidak valid");
                return "redirect:/admin/profile-settings-admin";
            }
            
            // Ambil user saat ini
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sesi user tidak valid");
                return "redirect:/login";
            }
            
            // Proses foto profil jika ada
            if (fotoProfil != null && !fotoProfil.isEmpty()) {
                try {
                    // Validasi ukuran file (maksimal 2MB)
                    if (fotoProfil.getSize() > 2 * 1024 * 1024) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Ukuran file terlalu besar (maksimal 2MB)");
                        return "redirect:/admin/profile-settings-admin";
                    }

                    // Validasi tipe file
                    String contentType = fotoProfil.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        redirectAttributes.addFlashAttribute("errorMessage", "File harus berupa gambar");
                        return "redirect:/admin/profile-settings-admin";
                    }
                    
                    // Buat direktori upload jika belum ada
                    String uploadPath = uploadDir + "/img/profile";
                    Path uploadDirPath = Paths.get(uploadPath);
                    if (!Files.exists(uploadDirPath)) {
                        Files.createDirectories(uploadDirPath);
                    }
                    
                    // Generate nama file unik
                    String fileName = UUID.randomUUID().toString() + "_" + fotoProfil.getOriginalFilename();
                    Path filePath = uploadDirPath.resolve(fileName);
                    
                    // Simpan file
                    Files.copy(fotoProfil.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    
                    // Hapus foto lama jika bukan default.png
                    if (!"default.png".equals(currentUser.getFotoProfil())) {
                        try {
                            Path oldFilePath = uploadDirPath.resolve(currentUser.getFotoProfil());
                            Files.deleteIfExists(oldFilePath);
                        } catch (IOException e) {
                            logger.warn("Gagal menghapus foto profil lama: ", e);
                        }
                    }
                    
                    // Update path foto profil di user
                    currentUser.setFotoProfil(fileName);
                    
                } catch (IOException e) {
                    logger.error("Error saat menyimpan foto profil: ", e);
                    redirectAttributes.addFlashAttribute("errorMessage", "Gagal menyimpan foto profil");
                    return "redirect:/admin/profile-settings-admin";
                }
            }
            
            // Simpan email lama
            String oldEmail = currentUser.getEmail();
            
            // Cek duplikasi email
            if (!email.equals(oldEmail) && userService.isEmailExists(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email sudah digunakan oleh pengguna lain");
                return "redirect:/admin/profile-settings-admin";
            }
            
            // Perbarui data user
            currentUser.setNamaLengkap(userName);
            currentUser.setEmail(email);
            
            String result = userService.updateProfile(currentUser);
            
            if ("Profil berhasil diperbarui".equals(result)) {
                // Update authentication jika email berubah
                if (!email.equals(oldEmail)) {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        email,
                        auth.getCredentials(),
                        auth.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(newAuth);
                }
                
                redirectAttributes.addFlashAttribute("successMessage", result);
                logger.info("Profil berhasil diperbarui untuk user: {}", email);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
                logger.warn("Gagal memperbarui profil: {}", result);
            }
            
        } catch (Exception e) {
            logger.error("Error pada saveProfileSettings: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan: " + e.getMessage());
        }
        
        return "redirect:/admin/profile-settings-admin";
    }

    @GetMapping("/security-settings-admin")
    public String showSecuritySettings(Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            model.addAttribute("userName", currentUser.getNamaLengkap());
            model.addAttribute("user", currentUser); // Untuk foto profil di sidebar
            return "admin/security-settings-admin";
        } catch (Exception e) {
            logger.error("Error pada showSecuritySettings: ", e);
            model.addAttribute("errorMessage", "Gagal memuat pengaturan keamanan: " + e.getMessage());
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
            // Validasi password kosong
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password saat ini tidak boleh kosong");
                return "redirect:/admin/security-settings-admin";
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak boleh kosong");
                return "redirect:/admin/security-settings-admin";
            }

            // Validasi konfirmasi password
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak cocok dengan konfirmasi password");
                return "redirect:/admin/security-settings-admin";
            }

            // Validasi password baru tidak boleh sama dengan password saat ini
            if (newPassword.equals(currentPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password baru tidak boleh sama dengan password saat ini");
                return "redirect:/admin/security-settings-admin";
            }

            // Proses update password
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
        return "redirect:/admin/security-settings-admin";
    }
    
    // Method helper untuk validasi email
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        return email.matches(emailRegex);
    }
}