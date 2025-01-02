package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.model.Product;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    private static final String CART_SESSION_KEY = "cart";
    
    private static final String PROMO_SESSION_KEY = "promoCode";

    public List<CartItem> getCart(HttpSession session) {
        // Mendapatkan keranjang dari session
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addToCart(HttpSession session, CartItem cartItem, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah produk harus lebih dari 0");
        }

            // Validasi stok produk
        Product product = cartItem.getProduct();
        if (product.getStok() < quantity) {
            throw new IllegalArgumentException("Stok produk tidak mencukupi");
        }        
        // Ambil keranjang belanja dari session
        List<CartItem> cart = getCart(session);

        // Cek apakah produk sudah ada di keranjang
        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProduct().getId() == cartItem.getProduct().getId()) {
                // Jika produk sudah ada, tambahkan quantity
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        // Jika produk belum ada di keranjang, tambahkan sebagai item baru
        if (!found) {
            cartItem.setQuantity(quantity); // Set quantity untuk item baru
            cart.add(cartItem);
        }

        // Simpan kembali keranjang ke session
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void updateCart(HttpSession session, int productId, int quantity) {
        // Memperbarui jumlah item di keranjang
        List<CartItem> cart = getCart(session);
        for (CartItem item : cart) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(quantity);
                break;
            }
        }
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeFromCart(HttpSession session, int productId) {
        // Menghapus item dari keranjang
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProduct().getId() == productId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        // Menghapus semua item dari keranjang
        session.setAttribute(CART_SESSION_KEY, new ArrayList<>());
        
    }
    public void setPromoCode(HttpSession session, String promoCode) {
    session.setAttribute(PROMO_SESSION_KEY, promoCode);
    }

    /**
     * Mengambil promo code dari session.
     */
    public String getPromoCode(HttpSession session) {
        return (String) session.getAttribute(PROMO_SESSION_KEY);
    }

    /**
     * Menghapus promo code dari session.
     */
    public void clearPromoCode(HttpSession session) {
        session.removeAttribute(PROMO_SESSION_KEY);
    }

    public double calculateSubtotal(HttpSession session) {
        List<CartItem> cart = getCart(session);
        return cart.stream()
                .mapToDouble(item -> {
                    double hargaSetelahDiskon = item.getProduct().getHarga() * (1 - (item.getProduct().getDiskon() / 100.0));
                    return hargaSetelahDiskon * item.getQuantity();
                })
                .sum();
    }
}