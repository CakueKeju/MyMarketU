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
            // Redirect ke halaman checkout
            return "redirect:/customer/orders/checkout/" + order.getId();
        } catch (IllegalArgumentException e) {
            // Tangani error dan kembalikan pesan ke halaman keranjang
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customer/cart";
        }
    }

    
    
   
    
    
    

    @GetMapping("/checkout/{orderId}")
    public String viewCheckoutPage(@PathVariable int orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Pesanan tidak ditemukan.");
        }

        // Persiapkan data untuk ditampilkan di halaman checkout
        List<OrderItem> orderItems = order.getOrderItems();
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);

        return "checkout"; // Nama file HTML yang akan digunakan
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
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Tangani jika pesanan tidak ditemukan atau userId tidak valid
        }
    }
}
 