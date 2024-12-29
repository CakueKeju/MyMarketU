/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cakwe.MyMarketU.repository;

import com.cakwe.MyMarketU.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
/**
 *
 * @author Cakue
 */
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByUserId(Long userId); // Mendapatkan semua transaksi berdasarkan user ID
    
    Optional<Transaction> findByInvoiceNumber(String invoiceNumber);

}