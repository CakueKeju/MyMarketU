package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.Order;
import com.cakwe.MyMarketU.model.OrderItem;
import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.model.Promo;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.repository.OrderRepository;
import com.cakwe.MyMarketU.repository.OrderItemRepository;
import com.cakwe.MyMarketU.repository.ProductRepository;
import com.cakwe.MyMarketU.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
    
    @Autowired
    private ProductRepository productRepository;

    public Order checkout(List<CartItem> cartItems, Long userId, String promoCode) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Keranjang belanja kosong");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));
        
        // Validasi stok untuk setiap item di keranjang
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProduct().getId())
                              .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan"));
            if (item.getProduct().getStok() < item.getQuantity()) {
                throw new IllegalArgumentException("Stok tidak mencukupi untuk produk: " + item.getProduct().getNama());
            }
        }

        // Hitung subtotal
        double subtotal = cartItems.stream()
                                   .mapToDouble(item -> item.getProduct().getHarga() * item.getQuantity())
                                   .sum();

        // Hitung diskon jika ada promo
        double discount = 0.0;
        if (promoCode != null && !promoCode.isEmpty()) {
            Promo promo = promoService.validatePromo(promoCode);
            discount = subtotal * (promo.getDiscountPercentage() / 100);
        }

        // Hitung total harga setelah diskon
        double totalPrice = subtotal - discount;


        // Buat dan simpan pesanan baru
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);
        
        Order savedOrder = orderRepository.save(order);

        // Simpan item pesanan
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItemRepository.save(orderItem);
        }

        return savedOrder;
    }

    public List<Order> getOrdersByUser(int userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pesanan tidak ditemukan"));
    }
}
