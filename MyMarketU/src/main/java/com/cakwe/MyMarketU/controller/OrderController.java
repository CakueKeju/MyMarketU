package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.model.Order;
import com.cakwe.MyMarketU.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Proses checkout
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestParam Long userId, 
                                           @RequestParam(required = false) String promoCode, 
                                           @RequestBody List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            // Proses checkout menggunakan OrderService
            Order order = orderService.checkout(cartItems, userId, promoCode);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Tangani jika ada error (misalnya promo tidak valid)
        }
    }

    // Mendapatkan daftar pesanan berdasarkan userId
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestParam int userId) {
        try {
            List<Order> orders = orderService.getOrdersByUser(userId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Tangani jika userId tidak valid
        }
    }

    // Mendapatkan detail pesanan berdasarkan orderId
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id, @RequestParam int userId) {
        try {
            Order order = orderService.getOrderById(id, userId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Tangani jika pesanan tidak ditemukan atau userId tidak valid
        }
    }
}
