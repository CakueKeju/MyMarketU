/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.TransactionDTO;
import com.cakwe.MyMarketU.model.TransactionItemDTO;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.service.TransactionService;
import com.cakwe.MyMarketU.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Cakue
 */
@Controller
@RequestMapping("/customer/cart")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public String addToCart(HttpSession session,
                        @RequestParam int productId,
                        @RequestParam int quantity,
                        RedirectAttributes redirectAttributes) {
        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kuantitas harus lebih besar dari 0.");
            return "redirect:/customer/homepage"; 
        }
        try {
            // Tambahkan item ke keranjang
            transactionService.addItemToCartInSession(session, productId, quantity);

            // Berikan pesan feedback kepada user
            redirectAttributes.addFlashAttribute("successMessage", "Item berhasil ditambahkan ke keranjang.");
        } catch (IllegalArgumentException e) {
            // Tangani error jika ada
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Tetap di halaman yang sama (homepage atau produk)
        return "redirect:/customer/homepage"; // Ganti sesuai halaman asal
    }
     @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<TransactionItemDTO> cart = transactionService.getCartFromSession(session);
        if (cart.isEmpty()) {
            model.addAttribute("errorMessage", "Keranjang Anda kosong.");
        }

        double total = cart.stream().mapToDouble(TransactionItemDTO::getPrice).sum();

        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", total);

        return "customer/cart";
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFromCart(HttpSession session,
                                            @RequestParam int productId) {
        try {
            // Hapus item dari keranjang
            List<TransactionItemDTO> cart = transactionService.removeItemFromCartInSession(session, productId);

            // Berikan respons sukses
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            // Berikan respons error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        // Ambil user saat ini
        User user = userService.getCurrentUser();

        // Ambil keranjang dari session
        List<TransactionItemDTO> cart = transactionService.getCartFromSession(session);
        if (cart.isEmpty()) {
            model.addAttribute("errorMessage", "Your cart is empty.");
            return "redirect:/customer/cart"; // Redirect kembali ke halaman cart jika keranjang kosong
        }

        // Tambahkan data yang diperlukan ke model untuk checkout.html
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalCost", cart.stream().mapToDouble(TransactionItemDTO::getPrice).sum());
        model.addAttribute("userName", user.getNamaLengkap());

        return "customer/checkout"; // Arahkan ke halaman checkout.html
    }
}
