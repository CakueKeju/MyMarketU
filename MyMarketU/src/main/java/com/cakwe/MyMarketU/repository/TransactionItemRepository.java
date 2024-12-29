/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cakwe.MyMarketU.repository;

import com.cakwe.MyMarketU.model.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author Cakue
 */
public interface TransactionItemRepository extends JpaRepository<TransactionItem, String> {
}