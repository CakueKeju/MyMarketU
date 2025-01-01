package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.CartItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    private final List<CartItem> cartItems = new ArrayList<>();

    public void addToCart(CartItem item) {
        cartItems.add(item);
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void removeFromCart(int productId) {
        cartItems.removeIf(item -> item.getProductId() == productId);
    }

    public double calculateSubtotal() {
        return cartItems.stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();
    }

    public void clearCart() {
        cartItems.clear();
    }
}