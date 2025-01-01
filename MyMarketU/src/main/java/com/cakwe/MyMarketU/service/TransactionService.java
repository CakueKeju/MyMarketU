/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.*;
import com.cakwe.MyMarketU.repository.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



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
    public TransactionDTO getOrCreateTransaction(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Transaction transaction = transactionRepository.findByUserIdAndStatus(userId, Transaction.TransactionStatus.PENDING);

        if (transaction == null) {
            transaction = new Transaction();
            transaction.setUser(user);
            transaction.setInvoiceNumber("INV-" + System.currentTimeMillis());
            transaction.setTotalCost(0.0);
            transaction.setStatus(Transaction.TransactionStatus.PENDING);
            transaction = transactionRepository.save(transaction);
        }

        return mapToDTO(transaction);
    }
    
    @Transactional
    public TransactionDTO createCart(User user) { 
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setInvoiceNumber("INV-" + System.currentTimeMillis());
        transaction.setTotalCost(0.0);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToDTO(savedTransaction);
    }

    @Transactional
    public TransactionDTO addItemToCart(int transactionId, int productId, int quantity) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStok() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        TransactionItem item = new TransactionItem();
        item.setTransaction(transaction);
        item.setProduct(product);
        item.setQuantity(quantity);
        transactionItemRepository.save(item);

        product.setStok(product.getStok() - quantity);
        productRepository.save(product);

        transaction.setTotalCost(calculateTotalCost(transaction));
        transactionRepository.save(transaction);

        return mapToDTO(transaction);
    }

    @Transactional
    public List<TransactionItemDTO> getCartItems(int transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        return transaction.getItems().stream()
            .map(item -> new TransactionItemDTO(
                    item.getProduct().getId(),
                    item.getProduct().getNama(),
                    item.getQuantity(),
                    item.getProduct().getHarga() * item.getQuantity()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public TransactionDTO checkout(int transactionId, String evidence) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        transaction.setTransactionEvidence(evidence);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return mapToDTO(updatedTransaction);
    }

    @Transactional
    public TransactionDTO updateStatus(int transactionId, Transaction.TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        transaction.setStatus(status);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return mapToDTO(updatedTransaction);
    }

    private double calculateTotalCost(Transaction transaction) {
    return transaction.getItems().stream()
            .mapToDouble(item -> {
                double discount = item.getProduct().getDiskon();
                double discountedPrice = item.getProduct().getHarga() * (1 - (discount / 100));
                return discountedPrice * item.getQuantity();
            })
            .sum();
}

    private TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setInvoiceNumber(transaction.getInvoiceNumber());
        dto.setTotalCost(transaction.getTotalCost());
        dto.setStatus(transaction.getStatus());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setItems(transaction.getItems().stream()
                .map(item -> new TransactionItemDTO(
                        item.getProduct().getId(),
                        item.getProduct().getNama(),
                        item.getQuantity(),
                        item.getProduct().getHarga() * item.getQuantity()
                ))
                .collect(Collectors.toList()));
        return dto;
    }
    
    @Transactional
    public TransactionDTO increaseItemInCart(int transactionId, int productId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        TransactionItem item = transaction.getItems().stream()
                .filter(i -> i.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStok() <= 0) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        item.setQuantity(item.getQuantity() + 1);
        product.setStok(product.getStok() - 1);

        transactionItemRepository.save(item);
        productRepository.save(product);

        transaction.setTotalCost(calculateTotalCost(transaction));
        transactionRepository.save(transaction);

        return mapToDTO(transaction);
    }
    
    @Transactional
    public TransactionDTO decreaseItemInCart(int transactionId, int productId, boolean confirmDeletion) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        TransactionItem item = transaction.getItems().stream()
                .filter(i -> i.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            product.setStok(product.getStok() + 1);
            transactionItemRepository.save(item);
            productRepository.save(product);
        } else if (confirmDeletion) {
            transaction.getItems().remove(item);
            transactionItemRepository.delete(item);
            product.setStok(product.getStok() + item.getQuantity());
            productRepository.save(product);
        } else {
            throw new IllegalStateException("Confirmation required to delete item.");
        }

        transaction.setTotalCost(calculateTotalCost(transaction));
        transactionRepository.save(transaction);

        return mapToDTO(transaction);
    }

}