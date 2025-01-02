/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cakwe.MyMarketU.repository;
import com.cakwe.MyMarketU.model.Product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Cakue
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {
@Query("SELECT p FROM Product p WHERE LOWER(p.nama) LIKE LOWER(CONCAT('%', :query, '%'))")
List<Product> findByNamaContainingIgnoreCase(@Param("query") String query);
    
    @Query("SELECT DISTINCT p.kategori FROM Product p")
    List<String> findDistinctCategories();
}
