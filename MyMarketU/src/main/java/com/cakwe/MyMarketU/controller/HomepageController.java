package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class HomepageController {

    @Autowired
    private ProductService productService;

    @GetMapping("/homepage")
    public String showHomepage(Model model) {
        // Ambil semua produk dari database menggunakan ProductService
        model.addAttribute("products", productService.getAllProducts());
        return "customer/homepage"; // Mengarahkan ke template homepage.html
    }
}
