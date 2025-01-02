/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cakwe.MyMarketU.repository;

import com.cakwe.MyMarketU.model.Penilaian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenilaianRepository extends JpaRepository<Penilaian, Integer> {
    List<Penilaian> findByIdProduk(Long idProduk);
    List<Penilaian> findByIdPelanggan(Long idPelanggan);  // Ubah parameter menjadi Long
}
