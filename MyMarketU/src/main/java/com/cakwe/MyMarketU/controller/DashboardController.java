package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.repository.ProductRepository;
import com.cakwe.MyMarketU.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        // Total Products
        long totalProducts = productRepository.count();
        model.addAttribute("totalProducts", totalProducts);
        
        // Total Customers (user dengan role_id = 2)
        // Pastikan angka 2 sesuai dengan role_id customer di database
        long totalCustomers = userRepository.countByRole_Id(2);
        model.addAttribute("totalCustomers", totalCustomers);
        
        // Low Stock Products (stok < 20)
        List<Product> lowStockProducts = productRepository.findAll().stream()
                .filter(p -> p.getStok() < 20)
                .collect(Collectors.toList());
        model.addAttribute("lowStockProducts", lowStockProducts);
        
        // Tambahkan log untuk debugging
        System.out.println("Total Customers: " + totalCustomers);
        
        return "index-admin";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Model model) {
        return "customer";
    }
}