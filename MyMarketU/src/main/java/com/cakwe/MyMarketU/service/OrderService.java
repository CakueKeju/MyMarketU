/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.Order;
import com.cakwe.MyMarketU.model.OrderItem;
import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.model.Promo;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.repository.OrderRepository;
import com.cakwe.MyMarketU.repository.OrderItemRepository;
import com.cakwe.MyMarketU.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
/**
 *
 * @author Cakue
 */
@Service
public class OrderService {
    @Autowired
    private PromoService promoService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public Order checkout(HttpSession session, Long userId, String promoCode) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
        throw new IllegalStateException("Keranjang belanja kosong");
        }

        // Validasi stok untuk setiap item di keranjang
        for (CartItem item : cart) {
            if (item.getProduct().getStok() < item.getQuantity()) {
                throw new IllegalArgumentException("Stok tidak mencukupi untuk produk: " + item.getProduct().getNama());
            }
        }

        double subtotal = cart.stream().mapToDouble(item -> item.getProduct().getHarga() * item.getQuantity()).sum();
        
        double discount = 0.0;
        if (promoCode != null && !promoCode.isEmpty()) {
            Promo promo = promoService.validatePromoCode(promoCode);
            discount = subtotal * (promo.getDiscountPercentage() / 100);
        }

        double totalPrice = subtotal - discount;
          // Ambil User berdasarkan userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Pengguna tidak ditemukan dengan ID: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);


        for (CartItem cartItem : cart) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItemRepository.save(orderItem);
        }


        // Kosongkan keranjang
        session.setAttribute("cart", new ArrayList<>());

        return savedOrder;
    }
    
    public List<Order> getOrdersByUser(int userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public Order getOrderById(int id, int userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pesanan tidak ditemukan"));

        if (order.getUser().getId() != userId) {
            throw new IllegalArgumentException("Anda tidak memiliki akses ke pesanan ini");
        }

        return order;
    }
}
