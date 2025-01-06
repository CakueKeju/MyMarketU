package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.model.CartItemDTO;
import com.cakwe.MyMarketU.service.CartService;
import com.cakwe.MyMarketU.service.PromoService;

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
    private PromoService promoService;
    
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItemDTO cartItemDTO) {
        System.out.println("Received request: " + cartItemDTO);
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
        double discount = 0;
        String currentPromoCode = cartService.getCurrentPromoCode();
        if (currentPromoCode != null) {
            discount = promoService.calculateDiscount(subtotal, currentPromoCode);
        }
        double totalPrice = subtotal - discount;

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("promoCode", currentPromoCode);
        return "customer/cart";
    }

    @GetMapping("/apply-promo")
    public ResponseEntity<?> applyPromo(@RequestParam String promoCode, @RequestParam double subtotal) {
        try {
            System.out.println("Received subtotal: " + subtotal); // Debugging subtotal
            double discount = promoService.calculateDiscount(subtotal, promoCode);
            return ResponseEntity.ok(discount);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage()); // Debugging error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
