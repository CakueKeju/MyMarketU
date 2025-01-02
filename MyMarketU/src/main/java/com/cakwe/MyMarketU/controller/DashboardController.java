package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.repository.ProductRepository;
import com.cakwe.MyMarketU.repository.UserRepository;
import com.cakwe.MyMarketU.service.ProductService;
import com.cakwe.MyMarketU.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired 
    private UserRepository userRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        try {
            // Ambil data user yang sedang login
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("user", currentUser);
                model.addAttribute("userName", currentUser.getNamaLengkap());
                logger.debug("Foto profil user: {}", currentUser.getFotoProfil());
            }

            // Hitung total products
            long totalProducts = productRepository.count();
            model.addAttribute("totalProducts", totalProducts);

            // Hitung total customers
            long totalCustomers = userRepository.countByRole_Id(2);
            model.addAttribute("totalCustomers", totalCustomers);

            // Ambil produk dengan stok rendah
            List<Product> lowStockProducts = productRepository.findAll().stream()
                .filter(p -> p.getStok() < 20)
                .collect(Collectors.toList());
            model.addAttribute("lowStockProducts", lowStockProducts);

            logger.info("Dashboard loaded successfully for user: {}", 
                       currentUser != null ? currentUser.getNamaLengkap() : "unknown");
            
            return "admin/index-admin";
            
        } catch (Exception e) {
            logger.error("Error loading dashboard: ", e);
            model.addAttribute("errorMessage", "Gagal memuat dashboard: " + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/admin/products-admin")
    public String productsAdmin() {
        return "redirect:/admin/product/list";
    }
    
    @GetMapping("/admin/orders-admin") 
    public String ordersAdmin() {
        return "admin/orders-admin";
    }
    
    @GetMapping("/admin/reports-admin")
    public String reportsAdmin() {
        return "admin/reports-admin";
    }
    
    //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    
    @GetMapping("/customer/homepage")
    public String customerHomepage(Model model, HttpSession session) {
        // Ambil pengguna yang sedang login
         User user = userService.getCurrentUser();
        if (user == null) {
            return "redirect:/login"; 
        }

        // Ambil daftar produk untuk ditampilkan di homepage
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);

        // Tambahkan nama user untuk dipakai di halaman
        model.addAttribute("userName", user.getNamaLengkap());

        return "customer/homepage";
    }
}