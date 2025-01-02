/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.service.CartService;
import com.cakwe.MyMarketU.service.ProductService;
import com.cakwe.MyMarketU.service.PromoService;
import jakarta.servlet.http.HttpSession;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 *
 * @author Cakue
 */
@Controller
@RequestMapping("/customer/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private PromoService promoService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(HttpSession session) {
        List<CartItem> cart = cartService.getCart(session);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(HttpSession session, @RequestParam int productId, @RequestParam int quantity) {
        Product product = productService.getProductById(productId);

        // Buat CartItem baru
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);

        // Tambahkan ke keranjang
        cartService.addToCart(session, cartItem, quantity);

        return ResponseEntity.ok("Produk berhasil ditambahkan ke keranjang");
    }


    @PutMapping("/{productId}")
    public ResponseEntity<String> updateCart(HttpSession session, @PathVariable int productId, @RequestParam int quantity) {
        cartService.updateCart(session, productId, quantity);
        return ResponseEntity.ok("Jumlah produk berhasil diperbarui");
    }

    @PostMapping("/remove")
    public String removeFromCart(HttpSession session, @RequestParam int productId) {
        cartService.removeFromCart(session, productId);
        return "redirect:/customer/cart/view";
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.ok("Keranjang berhasil dikosongkan");
    }
    
    
    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        // Ambil keranjang dari session
        List<CartItem> cartItems = cartService.getCart(session);

         // Hitung subtotal (harga asli tanpa diskon)
        double subtotal = cartService.calculateSubtotal(session);

        // Ambil promo code dari session
        String promoCode = cartService.getPromoCode(session);
        
        // Hitung diskon jika ada promo
       double discount = 0.0;
       if (promoCode != null && !promoCode.isEmpty()) {
           try {
               discount = promoService.calculateDiscount(subtotal, promoCode); // Gunakan PromoService
           } catch (IllegalArgumentException e) {
               model.addAttribute("promoError", "Kode promo tidak valid atau tidak berlaku.");
               cartService.clearPromoCode(session); // Hapus promo yang tidak valid
           }
       }

        // Hitung total setelah diskon
        double totalPrice = subtotal - discount;

        // Tambahkan data ke model
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal); // Harga asli tanpa diskon
        model.addAttribute("discount", discount); // Total diskon
        model.addAttribute("totalPrice", totalPrice); // Total setelah diskon
        model.addAttribute("promoCode", promoCode);

        return "customer/cart"; // Mengarah ke cart.html
    }
}

