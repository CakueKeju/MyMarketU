/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cakwe.MyMarketU.repository;

import com.cakwe.MyMarketU.model.Promo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 *
 * @author Cakue
 */
public interface PromoRepository extends JpaRepository<Promo, Long> {
    Optional<Promo> findByCodeAndActive(String code, boolean active);
}
