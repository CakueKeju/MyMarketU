package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.*;
import com.cakwe.MyMarketU.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String registerUser(User user){
        if(!user.getNamaLengkap().matches("^[a-zA-Z\\s]+$")){
            return "Nama hanya boleh mengandung huruf dan spasi";
        }
        
        if(userRepository.existsByEmail(user.getEmail())){
            return "Email sudah terdaftar";
        }
        
        if(userRepository.existsByNim(user.getNim())){
            return "NIM sudah terdaftar";
        }
        
        if (user.getPassword().length() < 8) {
            return "Password minimal 8 karakter";
        }
        
        try{
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if (user.getRole() == null) {
                Role defaultRole = new Role();
                defaultRole.setId(1L);
                defaultRole.setNama("Customer");
                user.setRole(defaultRole);
            }

            User savedUser = userRepository.save(user);
            return "Pendaftaran berhasil";
            
        }catch(Exception e) {
            logger.error("Error saat pendaftaran user: ", e);
            return "Pendaftaran gagal: " + e.getMessage();
        }
    }
    
    public User authenticate(String email, String password) {
        logger.debug("Mencoba autentikasi untuk email: {}", email);
        
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            logger.debug("User ditemukan: {}", userOptional.get());
        } else {
            logger.debug("User dengan email {} tidak ditemukan.", email);
        }
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.debug("User ditemukan: {}", user.getEmail());
            logger.debug("Hash tersimpan: {}", user.getPassword());

            if (passwordEncoder.matches(password, user.getPassword())) {
                logger.info("Password cocok untuk email: {}", email);
                return user;
            } else {
                logger.warn("Password tidak cocok untuk email: {}", email);
            }
        } else {
            logger.warn("User dengan email {} tidak ditemukan", email);
        }
        
        return null;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Mengambil data user untuk: {}", auth.getName());
        return userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    public String updateProfile(User user) {
        try {
            if(!user.getNamaLengkap().matches("^[a-zA-Z\\s]+$")){
                return "Nama hanya boleh mengandung huruf dan spasi";
            }
            
            Optional<User> existingUser = userRepository.findById(user.getId());
            if(!existingUser.isPresent()) {
                return "User tidak ditemukan";
            }
            
            User currentUser = existingUser.get();
            
            currentUser.setNamaLengkap(user.getNamaLengkap());
            currentUser.setEmail(user.getEmail());
            currentUser.setUpdatedAt(LocalDateTime.now());
            
            // Cek email baru jika berbeda dengan yang lama
            if (!currentUser.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(user.getEmail())) {
                    return "Email sudah digunakan oleh pengguna lain";
                }
                currentUser.setEmail(user.getEmail());
            }
            
            // Cek NIM baru jika berbeda dengan yang lama
            if (!currentUser.getNim().equals(user.getNim())) {
                if (userRepository.existsByNim(user.getNim())) {
                    return "NIM sudah digunakan oleh pengguna lain";
                }
                currentUser.setNim(user.getNim());
            }
            
            userRepository.save(currentUser);
            logger.info("Profil berhasil diperbarui untuk user: {}", currentUser.getEmail());
            return "Profil berhasil diperbarui";
            
        } catch (Exception e) {
            logger.error("Error saat memperbarui profil: ", e);
            return "Gagal memperbarui profil: " + e.getMessage();
        }
    }
    
    public String updatePassword(Long userId, String currentPassword, String newPassword) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if(!userOpt.isPresent()) {
                return "User tidak ditemukan";
            }
            
            User user = userOpt.get();
            
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                logger.warn("Percobaan password saat ini salah untuk user ID: {}", userId);
                return "Password saat ini tidak benar";
            }
            
            if (newPassword.length() < 8) {
                return "Password baru minimal 8 karakter";
            }
            
            if (!newPassword.matches(".*[A-Z].*")) {
                return "Password harus mengandung minimal 1 huruf besar";
            }
            if (!newPassword.matches(".*[a-z].*")) {
                return "Password harus mengandung minimal 1 huruf kecil";
            }
            if (!newPassword.matches(".*[0-9].*")) {
                return "Password harus mengandung minimal 1 angka";
            }
            
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            
            userRepository.save(user);
            logger.info("Password berhasil diperbarui untuk user ID: {}", userId);
            return "Password berhasil diperbarui";
            
        } catch (Exception e) {
            logger.error("Error saat memperbarui password: ", e);
            return "Gagal memperbarui password: " + e.getMessage();
        }
    }
    
    public String updateUserStatus(Long userId, Integer status) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if(!userOpt.isPresent()) {
                return "User tidak ditemukan";
            }
            
            User user = userOpt.get();
            
            if (status != 0 && status != 1) {
                return "Status tidak valid. Harus 0 atau 1";
            }
            
            user.setIsActive(status);
            user.setUpdatedAt(LocalDateTime.now());
            
            userRepository.save(user);
            logger.info("Status berhasil diperbarui menjadi {} untuk user ID: {}", status, userId);
            return "Status user berhasil diperbarui";
            
        } catch (Exception e) {
            logger.error("Error saat memperbarui status user: ", e);
            return "Gagal memperbarui status user: " + e.getMessage();
        }
    }

    private boolean validatePassword(String password) {
        return password != null &&
               password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*[0-9].*");
    }

    public boolean isEmailExists(String email) {
    User currentUser = getCurrentUser();
    Optional<User> userWithEmail = userRepository.findByEmail(email);
    
    // Mengembalikan true jika email exists dan bukan milik user saat ini
    return userWithEmail.isPresent() && !userWithEmail.get().getId().equals(currentUser.getId());
    }
    
    public User getUserById(Long userId) {
        // Validasi parameter
        if (userId == null) {
            throw new IllegalArgumentException("User ID tidak boleh null");
        }

        // Cari user berdasarkan ID
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User dengan ID " + userId + " tidak ditemukan"));
    }

}
