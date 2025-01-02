package com.cakwe.MyMarketU.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "penilaian")
public class Penilaian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String komentar;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private Long idProduk;

    @Column(nullable = false)
    private Long idPelanggan;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Penilaian() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Penilaian(String komentar, int rating, Long idProduk, Long idPelanggan) {
        this.komentar = komentar;
        this.rating = rating;
        this.idProduk = idProduk;
        this.idPelanggan = idPelanggan;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating harus antara 1 dan 5");
        }
        this.rating = rating;
    }

    public Long getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(Long idProduk) {
        this.idProduk = idProduk;
    }

    public Long getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(Long idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Method untuk update timestamp
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // toString method untuk debugging
    @Override
    public String toString() {
        return "Penilaian{" +
                "id=" + id +
                ", komentar='" + komentar + '\'' +
                ", rating=" + rating +
                ", idProduk=" + idProduk +
                ", idPelanggan=" + idPelanggan +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}