package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.model.Transaction;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.repository.ProductRepository;
import com.cakwe.MyMarketU.repository.UserRepository;
import com.cakwe.MyMarketU.service.TransactionService;
import com.cakwe.MyMarketU.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;



@Controller
public class DashboardController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired 
    private UserRepository userRepository;
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
    long totalProducts = productRepository.count();
    model.addAttribute("totalProducts", totalProducts);
    
    long totalCustomers = userRepository.countByRole_Id(2);
    model.addAttribute("totalCustomers", totalCustomers);
    
    List<Product> lowStockProducts = productRepository.findAll().stream()
            .filter(p -> p.getStok() < 20)
            .collect(Collectors.toList());
    model.addAttribute("lowStockProducts", lowStockProducts);
    
    String userName = (String) session.getAttribute("userName");
    model.addAttribute("userName", userName);
    
    System.out.println("Total Customers: " + totalCustomers);
    
    return "admin/index-admin";
    }
    
    @GetMapping("/admin/products-admin")
    public String productsAdmin() {
        return "redirect:/admin/product/list";  // Diubah untuk redirect ke product list
    }
    
    @GetMapping("/admin/orders-admin") 
    public String ordersAdmin() {
        return "admin/orders-admin";
    }
    
    @GetMapping("/admin/reports-admin")
    public String reportsAdmin() {
        return "admin/reports-admin";
    }
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping("/customer/homepage")
    public String customerHomepage(Model model, HttpSession session) {
        // Ambil user saat ini menggunakan UserService
        User user = userService.getCurrentUser();

        // Cek apakah ada transaksi PENDING untuk user ini
        Transaction transaction = transactionService.getPendingTransaction(user.getId());
        if (transaction != null) {
            session.setAttribute("transactionId", transaction.getId());
        } else {
            session.removeAttribute("transactionId"); // Hapus jika tidak ada transaksi PENDING
        }

        // Ambil daftar produk untuk ditampilkan
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("userName", user.getNamaLengkap()); // Tambahkan nama user ke halaman

        return "customer/homepage";
    }

}