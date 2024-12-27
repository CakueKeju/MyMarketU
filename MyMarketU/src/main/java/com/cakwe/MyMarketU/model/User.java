/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package com.cakwe.MyMarketU.model;

import java.time.LocalDateTime;        
import jakarta.persistence.*;
/**
*
* @author Cakue
*/
@Entity
@Table(name = "users")
public class User {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   @Column(name = "nama_lengkap", nullable = false)
   private String namaLengkap;
   
   @Column(nullable = false, unique = true)
   private String email;

   @Column(nullable = false)
   private String password;
   
   @Column(nullable = false, unique = true)
   private String nim;

   @Column(name = "foto_profil", nullable = false)
   private String fotoProfil = "default.png";
   
   @Column(name = "created_at", nullable = false)
   private LocalDateTime createdAt;

   @Column(name = "updated_at", nullable = false)
   private LocalDateTime updatedAt;
   
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "role_id", nullable = false)
   private Role role;

   @Column(name = "phone", nullable = true)
   private String phone;
   
   @Column(name = "status", nullable = false)
   private String status = "Active";
   
   @Column(name = "total_orders")
   private Integer totalOrders = 0;
   
   @Column(name = "total_spent")
   private Double totalSpent = 0.0;
   
   @Column(name = "last_order")
   private LocalDateTime lastOrder;

   //Getter dan Setter
   public Long getId() {
       return id;
   }

   public void setId(Long id) {
       this.id = id;
   }

   public String getNamaLengkap() {
       return namaLengkap;
   }

   public void setNamaLengkap(String namaLengkap) {
       this.namaLengkap = namaLengkap;
   }

   public String getEmail() {
       return email;
   }

   public void setEmail(String email) {
       this.email = email;
   }

   public String getPassword() {
       return password;
   }

   public void setPassword(String password) {
       this.password = password;
   }

   public String getNim() {
       return nim;
   }

   public void setNim(String nim) {
       this.nim = nim;
   }

   public String getFotoProfil() {
       return fotoProfil;
   }

   public void setFotoProfil(String fotoProfil) {
       this.fotoProfil = fotoProfil;
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

   public Role getRole() {
       return role;
   }

   public void setRole(Role role) {
       this.role = role;
   }
   
   public String getPhone() {
       return phone;
   }

   public void setPhone(String phone) {
       this.phone = phone; 
   }

   public String getStatus() {
       return status;
   }

   public void setStatus(String status) {
       this.status = status;
   }

   public Integer getTotalOrders() {
       return totalOrders;
   }

   public void setTotalOrders(Integer totalOrders) {
       this.totalOrders = totalOrders;
   }

   public Double getTotalSpent() {
       return totalSpent;
   }

   public void setTotalSpent(Double totalSpent) {
       this.totalSpent = totalSpent;
   }

   public LocalDateTime getLastOrder() {
       return lastOrder;
   }

   public void setLastOrder(LocalDateTime lastOrder) {
       this.lastOrder = lastOrder;
   }
}