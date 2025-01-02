/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.service;

import com.cakwe.MyMarketU.model.Promo;
import com.cakwe.MyMarketU.repository.PromoRepository;
import java.util.Optional;
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

    public Promo validatePromo(String promoCode) {
        if (promoCode == null || promoCode.isEmpty()) {
            throw new IllegalArgumentException("Kode promo tidak boleh kosong.");
        }

        Optional<Promo> promoOpt = promoRepository.findByCode(promoCode);
        if (promoOpt.isEmpty()) {
            throw new IllegalArgumentException("Kode promo tidak ditemukan.");
        }

        Promo promo = promoOpt.get();

        if (!promo.isActive()) {
            throw new IllegalArgumentException("Kode promo sudah tidak aktif.");
        }

    return promo;
}

    public double calculateDiscount(double subtotal, String promoCode) {
        Promo promo = validatePromo(promoCode);

        if (subtotal < promo.getMinimumSpend()) {
            throw new IllegalArgumentException("Total belanja tidak memenuhi syarat minimum promo.");
        }

        double discount = promo.getDiscountPercentage() / 100.0 * subtotal;

        return Math.min(discount, promo.getMaximumDiscount());
    }

}
