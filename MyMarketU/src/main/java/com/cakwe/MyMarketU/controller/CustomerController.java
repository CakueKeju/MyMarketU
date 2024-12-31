package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.repository.UserRepository;
import com.cakwe.MyMarketU.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class CustomerController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    @GetMapping("/customersmenu-admin")
    public String showCustomerPage(Model model) {
        List<User> customers = userRepository.findAllByRoleId(2);
        Long totalCustomers = userRepository.countByRole_Id(2);
        
        // Hitung pelanggan baru bulan ini
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        Long newCustomersThisMonth = customers.stream()
                .filter(user -> user.getCreatedAt().isAfter(startOfMonth))
                .count();
        
        // Set attribute untuk tampilan
        model.addAttribute("customers", customers);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("newCustomersThisMonth", newCustomersThisMonth);
        model.addAttribute("activeCustomers", totalCustomers);
        model.addAttribute("userName", "Admin");
        
        return "admin/customersmenu-admin";
    }

    @GetMapping("/customers/search")
    public String searchCustomers(@RequestParam String query, Model model) {
        List<User> customers;
        if (query != null && !query.trim().isEmpty()) {
            customers = userRepository.findAllByRoleId(2).stream()
                .filter(user -> 
                    user.getNamaLengkap().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    user.getNim().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        } else {
            customers = userRepository.findAllByRoleId(2);
        }
        
        model.addAttribute("customers", customers);
        model.addAttribute("totalCustomers", userRepository.countByRole_Id(2));
        return "admin/customersmenu-admin";
    }

    @GetMapping("/customers/filter")
    public String filterCustomers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            Model model) {
        
        List<User> customers = userRepository.findAllByRoleId(2);
        
        if (status != null && !status.equals("all")) {
            LocalDateTime threshold = LocalDateTime.now().minusMonths(1);
            customers = customers.stream()
                .filter(user -> {
                    switch (status) {
                        case "new":
                            return user.getCreatedAt().isAfter(threshold);
                        case "active":
                            return user.getIsActive() == 1;
                        case "inactive":
                            return user.getIsActive() == 0;
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
        }
        
        if (sortBy != null) {
            switch (sortBy) {
                case "name":
                    customers.sort(Comparator.comparing(User::getNamaLengkap));
                    break;
                case "dateJoined":
                    customers.sort(Comparator.comparing(User::getCreatedAt).reversed());
                    break;
            }
        }
        
        model.addAttribute("customers", customers);
        model.addAttribute("totalCustomers", userRepository.countByRole_Id(2));
        return "admin/customersmenu-admin";
    }

    @GetMapping("/customers/view/{id}")
    public String viewCustomer(@PathVariable Long id, Model model) {
        try {
            User customer = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelanggan tidak ditemukan"));
            model.addAttribute("customer", customer);
            return "admin/customer-detail";
        } catch (Exception e) {
            return "redirect:/admin/customersmenu-admin";
        }
    }

    @PostMapping("/customers/{id}/toggle-status")
    public String toggleCustomerStatus(@PathVariable Long id) {
        try {
            User customer = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelanggan tidak ditemukan"));
            
            // Toggle nilai is_active antara 1 dan 0
            customer.setIsActive(customer.getIsActive() == 1 ? 0 : 1);
            customer.setUpdatedAt(LocalDateTime.now());
            userRepository.save(customer);
            
            return "redirect:/admin/customersmenu-admin";
        } catch (Exception e) {
            return "redirect:/admin/customersmenu-admin";
        }
    }

    @GetMapping("/customers/edit/{id}")
    public String showEditCustomerForm(@PathVariable Long id, Model model) {
        try {
            User customer = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelanggan tidak ditemukan"));
            model.addAttribute("customer", customer);
            return "admin/customer-edit";
        } catch (Exception e) {
            return "redirect:/admin/customersmenu-admin";
        }
    }

    @PostMapping("/customers/edit/{id}")
    public String updateCustomer(
            @PathVariable Long id,
            @RequestParam String namaLengkap,
            @RequestParam String email,
            @RequestParam String nim,
            Model model) {
        try {
            User customer = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pelanggan tidak ditemukan"));
            
            customer.setNamaLengkap(namaLengkap);
            customer.setEmail(email);
            customer.setNim(nim);
            customer.setUpdatedAt(LocalDateTime.now());
            
            userRepository.save(customer);
            return "redirect:/admin/customersmenu-admin";
        } catch (Exception e) {
            model.addAttribute("error", "Gagal memperbarui data pelanggan");
            return "redirect:/admin/customers/edit/" + id;
        }
    }
}