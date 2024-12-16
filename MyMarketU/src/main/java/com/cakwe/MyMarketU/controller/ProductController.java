/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.model.ProductDTO;
import com.cakwe.MyMarketU.service.ProductRepository;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Cakue
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductRepository repo;
    
    @GetMapping({"","/"})
    public String showProductList (Model model){
        List<Product> product = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("product",product);
        return "product/index";
    }
    
    @GetMapping("/create")
    public String showCreatePage (Model model){
        ProductDTO productDTO = new ProductDTO();
        List<String> categories = repo.findDistinctCategories();
        
        model.addAttribute("categories",categories);
        model.addAttribute("productDTO", productDTO);
        return "product/CreateProduct";
    }
    
    @PostMapping("/create")
    public String createProduct(@ModelAttribute ProductDTO productDTO, BindingResult result, Model model) {
        if (productDTO.getNama() == null || productDTO.getNama().trim().isEmpty()) {
            result.addError(new FieldError("productDTO", "nama", "Nama tidak boleh kosong"));
        }

        if (productDTO.getKategori() == null || productDTO.getKategori().trim().isEmpty()) {
            result.addError(new FieldError("productDTO", "kategori", "Kategori tidak boleh kosong"));
        }

        if (productDTO.getDeskripsi() == null || productDTO.getDeskripsi().trim().isEmpty()) {
            result.addError(new FieldError("productDTO", "deskripsi", "Deskripsi tidak boleh kosong"));
        }

        if (productDTO.getHarga() <= 0) {
            result.addError(new FieldError("productDTO", "harga", "Harga harus lebih dari 0"));
        }

        if (productDTO.getStok() < 0) {
            result.addError(new FieldError("productDTO", "stok", "Stok tidak boleh negatif"));
        }

        if (productDTO.getImageFile().isEmpty()){
            result.addError(new FieldError("productDTO", "imageFile", "File image tidak valid"));
        }
        if (result.hasErrors()) {
            return "product/CreateProduct"; 
        }
        
        MultipartFile image = productDTO.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
        
        try{
            String uploadDir = "public/images";
            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetPath = uploadPath.resolve(storageFileName);

            if (Files.exists(targetPath)) {
                Files.delete(targetPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, targetPath);
            }

        }catch (Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }
        Product product = new Product();
        product.setNama(productDTO.getNama());
        product.setKategori(productDTO.getKategori());
        product.setDeskripsi(productDTO.getDeskripsi());
        product.setHarga(productDTO.getHarga());
        product.setStok(productDTO.getStok());
        product.setNamaFileGambar(storageFileName);

        repo.save(product);
        
        return "redirect:/product"; 
    }
    
    @GetMapping("/edit")
    public String showEditPage (Model model,@RequestParam int id){
        try{
            Product product = repo.findById(id).get();
            model.addAttribute("product",product);
            
            ProductDTO productDTO = new  ProductDTO();
            productDTO.setNama(product.getNama());
            productDTO.setKategori(product.getKategori());
            productDTO.setDeskripsi(product.getDeskripsi());
            productDTO.setHarga(product.getHarga());
            
        }catch(Exception ex){
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/product";
        }
        
        return "product/EditProduct";
    
        
    }
}
