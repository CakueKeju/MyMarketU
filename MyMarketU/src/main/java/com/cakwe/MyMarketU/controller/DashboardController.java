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
       long totalProducts = productRepository.count();
       model.addAttribute("totalProducts", totalProducts);
       
       long totalCustomers = userRepository.countByRole_Id(2);
       model.addAttribute("totalCustomers", totalCustomers);
       
       List<Product> lowStockProducts = productRepository.findAll().stream()
               .filter(p -> p.getStok() < 20)
               .collect(Collectors.toList());
       model.addAttribute("lowStockProducts", lowStockProducts);
       
       System.out.println("Total Customers: " + totalCustomers);
       
       return "admin/index-admin";
   }

   @GetMapping("/customer/dashboard")
   public String customerDashboard(Model model) {
       return "customer";
   }

   @GetMapping("/admin/products-admin")
   public String productsAdmin() {
       return "admin/products-admin";
   }
   
   @GetMapping("/admin/orders-admin") 
   public String ordersAdmin() {
       return "admin/orders-admin";
   }
   
   @GetMapping("/admin/customersmenu-admin")
   public String customersAdmin() {
       return "admin/customersmenu-admin";
   }
   
   @GetMapping("/admin/reports-admin")
   public String reportsAdmin() {
       return "admin/reports-admin";
   }
   
   @GetMapping("/admin/profile-settings-admin")
   public String settingsAdmin() {
       return "admin/profile-settings-admin";
   }
}