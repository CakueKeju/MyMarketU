package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.model.CartItemDTO;
import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.repository.ProductRepository;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

     private final ProductRepository productRepository;

    // Simpan keranjang sementara (bisa diganti dengan database jika perlu)
    private final List<CartItem> cart = new ArrayList<>();

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public void addToCart(CartItemDTO cartItemDTO) {
        // Validasi jumlah
        if (cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Jumlah produk harus lebih dari 0.");
        }

        // Validasi produk
        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan dengan ID: " + cartItemDTO.getProductId()));

        // Cek stok
        if (product.getStok() < cartItemDTO.getQuantity()) {
            throw new IllegalArgumentException("Stok produk tidak mencukupi.");
        }

        // Cek apakah produk sudah ada di keranjang
        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                // Update jumlah produk
                item.setQuantity(item.getQuantity() + cartItemDTO.getQuantity());
                found = true;
                break;
            }
        }

        // Jika produk belum ada, tambahkan sebagai item baru
        if (!found) {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(cartItemDTO.getQuantity());
            cart.add(newItem);
        }
    }


    public void updateCart(int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah produk harus lebih dari 0.");
        }

        for (CartItem item : cart) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(quantity);
                return;
            }
        }

        throw new IllegalArgumentException("Produk tidak ditemukan di keranjang.");
    }

    public void removeFromCart(int productId) {
        cart.removeIf(item -> item.getProduct().getId() == productId);
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cart); // Kembalikan salinan untuk menghindari modifikasi langsung
    }


    public void clearCart() {
        cart.clear();
    }

    public double calculateSubtotal() {
        return cart.stream()
                .mapToDouble(item -> item.getProduct().getHarga() * item.getQuantity())
                .sum();
    }
}
