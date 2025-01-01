/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.*;
import com.cakwe.MyMarketU.repository.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
    private UserService userService;

    private static final String CART_SESSION_KEY = "cart";

    @Transactional
    public void addItemToCartInSession(HttpSession session, int productId, int quantity) {
        List<TransactionItemDTO> cart = getCartFromSession(session);

        // Cek apakah produk sudah ada di keranjang
        Optional<TransactionItemDTO> existingItem = cart.stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst();

        if (existingItem.isPresent()) {
            // Jika produk sudah ada, tambahkan quantity
            TransactionItemDTO item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Jika produk belum ada, tambahkan ke keranjang
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan"));

            if (product.getStok() < quantity) {
                throw new IllegalArgumentException("Stok tidak mencukupi untuk produk: " + product.getNama());
            }

            TransactionItemDTO newItem = new TransactionItemDTO(
                    product.getId(),
                    product.getNama(),
                    quantity,
                    product.getHarga() * (1 - (product.getDiskon() / 100)) // Harga setelah diskon
            );
            cart.add(newItem);
        }

        updateCartInSession(session, cart);
    }

    @Transactional
    public List<TransactionItemDTO> getCartFromSession(HttpSession session) {
        List<TransactionItemDTO> cart = (List<TransactionItemDTO>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart); // Inisialisasi session jika belum ada
        }
        return cart;
    }

    @Transactional
    public void updateCartInSession(HttpSession session, List<TransactionItemDTO> cart) {
        // Perbarui keranjang di session
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Transactional
    public List<TransactionItemDTO> removeItemFromCartInSession(HttpSession session, int productId) {
        List<TransactionItemDTO> cart = getCartFromSession(session);

        // Hapus item berdasarkan ID produk
        cart.removeIf(item -> item.getProductId() == productId);

        // Perbarui session
        updateCartInSession(session, cart);
        return cart;
    }

    @Transactional
    public TransactionDTO checkoutFromSession(HttpSession session, String evidence) {
        // Ambil user saat ini
        User user = userService.getCurrentUser();

        // Ambil keranjang dari session
        List<TransactionItemDTO> cart = getCartFromSession(session);
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Keranjang Anda kosong.");
        }

        // Buat transaksi baru
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setInvoiceNumber("INV-" + System.currentTimeMillis());
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setTransactionEvidence(evidence);

        // Proses setiap item di keranjang
        List<TransactionItem> items = cart.stream().map(dto -> {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan: " + dto.getProductName()));

            if (product.getStok() < dto.getQuantity()) {
                throw new IllegalArgumentException("Stok tidak mencukupi untuk produk: " + product.getNama());
            }

            // Kurangi stok produk
            product.setStok(product.getStok() - dto.getQuantity());
            productRepository.save(product);

            // Buat item transaksi
            TransactionItem item = new TransactionItem();
            item.setTransaction(transaction);
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            return item;
        }).collect(Collectors.toList());

        // Simpan transaksi dan total biaya
        transaction.setItems(items);
        transaction.setTotalCost(cart.stream().mapToDouble(TransactionItemDTO::getPrice).sum());
        transactionRepository.save(transaction);

        // Bersihkan keranjang dari session
        session.removeAttribute(CART_SESSION_KEY);

        return mapToDTO(transaction);
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
    public Transaction getPendingTransaction(Long userId) {
        return transactionRepository.findByUserIdAndStatus(userId, Transaction.TransactionStatus.PENDING);
    }
}