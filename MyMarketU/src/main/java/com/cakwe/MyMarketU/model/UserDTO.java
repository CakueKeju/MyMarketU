/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.model;
import jakarta.validation.constraints.*;
/**
 *
 * @author Cakue
 */
public class UserDTO {
    @NotEmpty(message = "Nama wajib diisi")
    @Size(min = 3, max = 20, message = "Nama harus memiliki panjang antara 3 hingga 20 karakter")
    private String username;

    @NotEmpty(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotEmpty(message = "NIM wajib diisi")
    @Size(min = 10, message = "Nim harus memiliki panjang minimal 10 karakter")
    private String nim;

    @NotEmpty(message = "Password wajib diisi")
    @Size(min = 8, message = "Password harus memiliki panjang minimal 8 karakter")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
