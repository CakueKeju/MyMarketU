/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.*;
import com.cakwe.MyMarketU.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 *
 * @author Cakue
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/cart")
    public ResponseEntity<Transaction> createCart(@RequestParam Long userId) {
        Transaction transaction = transactionService.createCart(userId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{transactionId}/items")
    public ResponseEntity<Transaction> addItemToCart(
            @PathVariable String transactionId,
            @RequestBody TransactionItemDTO itemDTO) {
        Transaction transaction = transactionService.addItemToCart(transactionId, itemDTO);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{transactionId}/checkout")
    public ResponseEntity<Transaction> checkout(
            @PathVariable String transactionId,
            @RequestParam String transactionEvidence) {
        Transaction transaction = transactionService.checkout(transactionId, transactionEvidence);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{transactionId}/status")
    public ResponseEntity<Transaction> updateTransactionStatus(
            @PathVariable String transactionId,
            @RequestParam Transaction.TransactionStatus status) {
        Transaction transaction = transactionService.updateTransactionStatus(transactionId, status);
        return ResponseEntity.ok(transaction);
    }
}
