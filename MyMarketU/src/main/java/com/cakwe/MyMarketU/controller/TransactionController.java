/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.*;
import com.cakwe.MyMarketU.repository.UserRepository;
import com.cakwe.MyMarketU.service.TransactionService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Cakue
 */
@RestController
@RequestMapping("/customer")
public class TransactionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/create-cart")
    public ResponseEntity<TransactionDTO> createCart(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")); 

        TransactionDTO transaction = transactionService.createCart(user);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<TransactionDTO> addToCart(@RequestParam int transactionId,
                                                     @RequestParam int productId,
                                                     @RequestParam int quantity) {
        TransactionDTO transaction = transactionService.addItemToCart(transactionId, productId, quantity);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/checkout")
    public ResponseEntity<TransactionDTO> checkout(@RequestParam int transactionId,
                                                    @RequestParam String evidence) {
        TransactionDTO transaction = transactionService.checkout(transactionId, evidence);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/update-status")
    public ResponseEntity<TransactionDTO> updateStatus(@RequestParam int transactionId,
                                                        @RequestParam Transaction.TransactionStatus status) {
        TransactionDTO transaction = transactionService.updateStatus(transactionId, status);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/cart")
    public ResponseEntity<List<TransactionItemDTO>> getCartItems(@RequestParam int transactionId) {
        List<TransactionItemDTO> cartItems = transactionService.getCartItems(transactionId);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/cart/increase")
    public ResponseEntity<TransactionDTO> increaseItemInCart(@RequestParam int transactionId,
                                                              @RequestParam int productId) {
        TransactionDTO updatedTransaction = transactionService.increaseItemInCart(transactionId, productId);
        return ResponseEntity.ok(updatedTransaction);
    }

    @PostMapping("/cart/decrease")
    public ResponseEntity<?> decreaseItemInCart(@RequestParam int transactionId,
                                                @RequestParam int productId,
                                                @RequestParam(required = false, defaultValue = "false") boolean confirm) {
        try {
            TransactionDTO updatedTransaction = transactionService.decreaseItemInCart(transactionId, productId, confirm);
            return ResponseEntity.ok(updatedTransaction);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("Confirmation required to delete item.");
        }
    }

    @PostMapping("/cart/confirm-remove")
    public ResponseEntity<TransactionDTO> confirmRemoveItemFromCart(@RequestParam int transactionId,
                                                                     @RequestParam int productId) {
        TransactionDTO updatedTransaction = transactionService.decreaseItemInCart(transactionId, productId, true);
        return ResponseEntity.ok(updatedTransaction);
    }
}
