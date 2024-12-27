package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.*;
import com.cakwe.MyMarketU.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
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
           return "Only letters and spaces are allowed";
       }
       
       if(userRepository.existsByEmail(user.getEmail())){
           return "Email is already registered";
       }
       
       if(userRepository.existsByNim(user.getNim())){
           return "NIM is already registered";
       }
       
       if (user.getPassword().length() < 8) {
           return "Password must be at least 8 characters";
       }
       
       try{
           LocalDateTime now = LocalDateTime.now();
           user.setCreatedAt(now);
           user.setUpdatedAt(now);
           
           user.setPassword(passwordEncoder.encode(user.getPassword()));

           if (user.getRole() == null) {
               Role defaultRole = new Role();
               defaultRole.setId(1L); // Default role ID
               defaultRole.setNama("Customer"); // Default role name
               user.setRole(defaultRole);
           }

           User savedUser = userRepository.save(user);
           return "Registration successful";
           
       }catch(Exception e) {
           logger.error("Error during user registration: ", e);
           return "Registration failed: " + e.getMessage();
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
           logger.debug("Stored hash: {}", user.getPassword());

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

   // Tambahan method untuk customer management
   public List<User> getAllCustomers() {
       return userRepository.findAll();
   }

   public long getNewCustomersThisMonth() {
       LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
       return userRepository.countByCreatedAtGreaterThanEqual(startOfMonth);
   }

   public long getActiveCustomers() {
       return userRepository.countByStatus("Active");
   }

   public void updateCustomerStatus(Long id, String status) {
       User user = userRepository.findById(id)
           .orElseThrow(() -> new RuntimeException("Customer not found"));
       user.setStatus(status);
       user.setUpdatedAt(LocalDateTime.now());
       userRepository.save(user);
   }

   public List<User> searchCustomers(String query) {
       return userRepository.findByNamaLengkapContainingOrEmailContaining(query, query);
   }

   public List<User> filterCustomers(String status, String sortBy) {
       if ("all".equals(status)) {
           return userRepository.findAll();
       }
       return userRepository.findByStatus(status);
   }

   public User getCustomerById(Long id) {
       return userRepository.findById(id)
           .orElseThrow(() -> new RuntimeException("Customer not found"));
   }

   public void updateCustomer(Long id, User updatedUser) {
       User user = getCustomerById(id);
       user.setNamaLengkap(updatedUser.getNamaLengkap());
       user.setEmail(updatedUser.getEmail());
       user.setPhone(updatedUser.getPhone());
       user.setUpdatedAt(LocalDateTime.now());
       userRepository.save(user);
   }
}