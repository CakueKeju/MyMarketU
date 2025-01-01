package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.CartItem;
import com.cakwe.MyMarketU.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/view")
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("subtotal", cartService.calculateSubtotal());
        return "customer/cart";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(
            @RequestParam int productId,
            @RequestParam String productName,
            @RequestParam double productPrice,
            @RequestParam int quantity,
            @RequestParam String imageName) {
        CartItem item = new CartItem(productId, productName, productPrice, quantity, imageName);
        cartService.addToCart(item);
        return "redirect:/customer/cart/view";
    }

    @PostMapping("/remove-from-cart")
    public String removeFromCart(@RequestParam int productId) {
        cartService.removeFromCart(productId);
        return "redirect:/customer/cart/view";
    }

    @GetMapping("/pay")
    public String pay(Model model) {
        cartService.clearCart();
        model.addAttribute("message", "Pembayaran berhasil!");
        return "redirect:/customer/cart/view";
    }
    
}