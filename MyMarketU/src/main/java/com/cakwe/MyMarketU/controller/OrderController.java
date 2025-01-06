package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.model.Order;
import com.cakwe.MyMarketU.model.OrderItem;
import com.cakwe.MyMarketU.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/customer/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Proses checkout
    @PostMapping("/checkout")
    public String checkout(@RequestParam Long userId,
                           @RequestParam(required = false) String promoCode,
                           @RequestBody List<CartItem> cartItems,
                           RedirectAttributes redirectAttributes) {
        // Validasi keranjang kosong
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Keranjang belanja kosong");
        }

        try {
            // Panggil service untuk memproses pesanan
            Order order = orderService.checkout(cartItems, userId, promoCode);
            redirectAttributes.addAttribute("orderId", order.getId());
            return "redirect:/customer/orders/checkout/" + order.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customer/cart";
        }
    }

    // Halaman detail checkout
    @GetMapping("/checkout/{orderId}")
    public String viewCheckoutPage(@PathVariable int orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        return "checkout"; // File HTML untuk halaman checkout
    }

    // Konfirmasi pesanan oleh pengguna
    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<?> userConfirmOrder(@PathVariable int orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                throw new IllegalArgumentException("Pesanan tidak ditemukan.");
            }
            return ResponseEntity.ok("Pesanan Anda telah dikonfirmasi dan akan diproses.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}