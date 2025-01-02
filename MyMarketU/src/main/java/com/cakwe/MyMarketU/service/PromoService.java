/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.Promo;
import com.cakwe.MyMarketU.repository.PromoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Cakue
 */
@Service
public class PromoService {

    @Autowired
    private PromoRepository promoRepository;

    public Promo validatePromoCode(String code) {
        return promoRepository.findByCodeAndActive(code, true)
                .orElseThrow(() -> new IllegalArgumentException("Kode promo tidak valid"));
    }
    
    public double calculateDiscount(double subtotal, String promoCode) {
        if (promoCode == null || promoCode.isEmpty()) {
            return 0.0;
        }
        Promo promo = validatePromoCode(promoCode); // Validasi promo
        return subtotal * (promo.getDiscountPercentage() / 100);
    }
}
