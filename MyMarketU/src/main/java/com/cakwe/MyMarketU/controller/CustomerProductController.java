package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CustomerProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/customer/product/{id}")
    public String showProductDetail(@PathVariable int id, Model model) {
        try {
            // Ambil produk berdasarkan ID dari database
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Produk dengan ID " + id + " tidak ditemukan."));

            // Tambahkan produk ke model untuk dikirim ke view
            model.addAttribute("product", product);
            
            return "customer/product-detail"; // Nama file JSP untuk detail produk
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404"; // Halaman error jika produk tidak ditemukan
        }
    }
}
