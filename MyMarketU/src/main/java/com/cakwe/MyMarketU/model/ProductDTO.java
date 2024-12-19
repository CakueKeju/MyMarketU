/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.model;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Cakue
 */
public class ProductDTO {
    @NotEmpty(message = "Nama wajib diisi")
    private String nama;
    
    @NotEmpty(message = "Kategori wajib dipilih")
    private String kategori;
    
    @Size(min = 10, message = "Deskripsi minimal 10 abjad")
    @Size(max = 20, message = "Deskripsi maksimal 2000 abjad")
    private String deskripsi;
    
    @Min(0)
    @Digits(integer = 8, fraction = 3, message = "Harga maksimal 99,999,99.99")
    private double harga;
    
    @Min(0)
    @Digits(integer = 4, fraction = 0, message = "Stok maksimal 9999")
    private int stok;
    private MultipartFile namaFileGambar;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public MultipartFile getNamaFileGambar() {
        return namaFileGambar;
    }

    public void setNamaFileGambar(MultipartFile namaFileGambar) {
        this.namaFileGambar = namaFileGambar;
    }

}
    