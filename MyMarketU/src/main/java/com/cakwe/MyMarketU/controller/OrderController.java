/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Order;
import com.cakwe.MyMarketU.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
/**
 *
 * @author Cakue
 */
@RestController
@RequestMapping("/customer/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(HttpSession session, 
                                           @RequestParam Long userId, 
                                           @RequestParam(required = false) String promoCode) {
        try {
            Order order = orderService.checkout(session, userId, promoCode);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid promo or other errors
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestParam int userId) {
        List<Order> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id, @RequestParam int userId) {
        try {
            Order order = orderService.getOrderById(id, userId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Handle order not found or access error
        }
    }
}
