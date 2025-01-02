package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.model.CartItemDTO;
import com.cakwe.MyMarketU.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/customer/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItemDTO cartItemDTO) {
        try {
            cartService.addToCart(cartItemDTO);
            return ResponseEntity.ok("Produk berhasil ditambahkan ke keranjang.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems() {
        return ResponseEntity.ok(cartService.getCartItems());
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCart(@RequestParam int productId, @RequestParam int quantity) {
        try {
            cartService.updateCart(productId, quantity);
            return ResponseEntity.ok("Jumlah produk berhasil diperbarui.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam int productId) {
        cartService.removeFromCart(productId);
        return ResponseEntity.ok("Produk berhasil dihapus dari keranjang.");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok("Keranjang berhasil dikosongkan.");
    }
    
    @GetMapping("/view")
    public String viewCart(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        double subtotal = cartItems.stream()
                                   .mapToDouble(item -> item.getProduct().getHarga() * item.getQuantity())
                                   .sum();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", 0); // Diskon default, bisa diubah
        model.addAttribute("totalPrice", subtotal); // Total harga
        return "customer/cart"; // Mengarahkan ke cart.html di templates
    }
}
