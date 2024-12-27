// Product.java
package com.cakwe.MyMarketU.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "produk")
public class Product {
   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private int id;
   
   private String nama;
   private String kategori;
   
   @NotNull
   @Min(0)
   private double harga;
   
   @Column(columnDefinition = "TEXT")
   private String deskripsi;
   
   @NotNull
   @Min(0)
   private int stok;
   
   @Min(0)
   private int diskon;
   
   private String namaFileGambar;

   // Getters and setters remain the same
   public int getId() {
       return id;
   }

   public void setId(int id) {
       this.id = id;
   }

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

   public double getHarga() {
       return harga;
   }

   public void setHarga(double harga) {
       this.harga = harga;
   }

   public String getDeskripsi() {
       return deskripsi;
   }

   public void setDeskripsi(String deskripsi) {
       this.deskripsi = deskripsi;
   }

   public int getStok() {
       return stok;
   }

   public void setStok(int stok) {
       this.stok = stok;
   }

   public String getNamaFileGambar() {
       return namaFileGambar;
   }

   public void setNamaFileGambar(String namaFileGambar) {
       this.namaFileGambar = namaFileGambar;
   }

   public int getDiskon() {
       return diskon;
   }

   public void setDiskon(int diskon) {
       this.diskon = diskon;
   }
}