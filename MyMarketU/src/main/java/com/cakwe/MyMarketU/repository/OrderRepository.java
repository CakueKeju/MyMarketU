/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cakwe.MyMarketU.repository;

import com.cakwe.MyMarketU.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Cakue
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(int userId); // Order berdasarkan pengguna
    List<Order> findByStatus(String status); 
}
