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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class CustomerController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    private void calculateAndAddStatistics(List<User> allCustomers, Model model) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfCurrentMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
        
        // Hitung statistik untuk bulan ini
        Long totalCustomers = (long) allCustomers.size();
        Long newCustomersThisMonth = allCustomers.stream()
                .filter(user -> user.getCreatedAt().isAfter(startOfCurrentMonth))
                .count();

        // Hitung statistik untuk bulan sebelumnya
        Long previousMonthTotal = allCustomers.stream()
                .filter(user -> user.getCreatedAt().isBefore(startOfCurrentMonth))
                .count();
        Long previousMonthNew = allCustomers.stream()
                .filter(user -> user.getCreatedAt().isAfter(startOfPreviousMonth) 
                               && user.getCreatedAt().isBefore(startOfCurrentMonth))
                .count();

        // Hitung persentase pertumbuhan
        Double totalGrowth = previousMonthTotal > 0 ? 
            ((totalCustomers - previousMonthTotal) / (double) previousMonthTotal) * 100.0 : 0.0;
        Double newGrowth = previousMonthNew > 0 ? 
            ((newCustomersThisMonth - previousMonthNew) / (double) previousMonthNew) * 100.0 : 0.0;

        // Set attribute untuk tampilan
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("newCustomersThisMonth", newCustomersThisMonth);
        model.addAttribute("totalCustomersGrowth", totalGrowth);
        model.addAttribute("newCustomersGrowth", newGrowth);
        model.addAttribute("userName", "Admin");
    }

    @GetMapping("/customersmenu-admin")
    public String showCustomerPage(Model model) {
        List<User> customers = userRepository.findAllByRoleId(2);
        calculateAndAddStatistics(customers, model);
        model.addAttribute("customers", customers);
        model.addAttribute("userName", "Admin");
        return "admin/customersmenu-admin";
    }

    @GetMapping("/customers/search")
    public String searchCustomers(@RequestParam String query, Model model) {
        List<User> allCustomers = userRepository.findAllByRoleId(2);
        List<User> filteredCustomers;
        
        if (query != null && !query.trim().isEmpty()) {
            filteredCustomers = allCustomers.stream()
                .filter(user -> 
                    user.getNamaLengkap().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    user.getNim().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        } else {
            filteredCustomers = allCustomers;
        }
        
        calculateAndAddStatistics(allCustomers, model);
        model.addAttribute("customers", filteredCustomers);
        model.addAttribute("userName", "Admin");
        
        return "admin/customersmenu-admin";
    }

    @GetMapping("/customers/filter")
    public String filterCustomers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            Model model) {
        
        List<User> allCustomers = userRepository.findAllByRoleId(2);
        List<User> filteredCustomers = new ArrayList<>(allCustomers);
        
        if (status != null && !status.equals("all")) {
            LocalDateTime threshold = LocalDateTime.now().minusMonths(1);
            filteredCustomers = filteredCustomers.stream()
                .filter(user -> {
                    switch (status) {
                        case "new":
                            return user.getCreatedAt().isAfter(threshold);
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
        }
        
        if (sortBy != null) {
            switch (sortBy) {
                case "name":
                    filteredCustomers.sort(Comparator.comparing(User::getNamaLengkap));
                    break;
                case "dateJoined":
                    filteredCustomers.sort(Comparator.comparing(User::getCreatedAt).reversed());
                    break;
            }
        }
        
        calculateAndAddStatistics(allCustomers, model);
        model.addAttribute("customers", filteredCustomers);
        model.addAttribute("userName", "Admin");
        
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

    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
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