/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.*;
import com.cakwe.MyMarketU.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 *
 * @author Cakue
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Transaction createCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setInvoiceNumber("INV-" + System.currentTimeMillis());
        transaction.setTotalCost(0.0);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction addItemToCart(String transactionId, TransactionItemDTO itemDTO) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStok() < itemDTO.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        TransactionItem item = new TransactionItem();
        item.setTransaction(transaction);
        item.setProduct(product);
        itemDTO.setQuantity(itemDTO.getQuantity());

        product.setStok(product.getStok() - itemDTO.getQuantity()); // Kurangi stok
        transactionItemRepository.save(item);

        // Update total cost
        double totalCost = transaction.getItems().stream()
                .mapToDouble(i -> i.getProduct().getHarga() * itemDTO.getQuantity())
                .sum();
        transaction.setTotalCost(totalCost);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction checkout(String transactionId, String transactionEvidence) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        transaction.setTransactionEvidence(transactionEvidence);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransactionStatus(String transactionId, Transaction.TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }
}