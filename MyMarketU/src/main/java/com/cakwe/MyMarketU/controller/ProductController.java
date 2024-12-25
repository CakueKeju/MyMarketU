/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.model.ProductDTO;
import com.cakwe.MyMarketU.repository.ProductRepository;
import jakarta.validation.Valid;
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
    public String createProduct(@Valid @ModelAttribute ProductDTO productDTO, BindingResult result) {
        if (productDTO.getNamaFileGambar().isEmpty()){
            result.addError(new FieldError("productDTO", "imageFile", "File image tidak valid"));
        }
        if (result.hasErrors()) {
            return "product/CreateProduct"; 
        }
        
        MultipartFile image = productDTO.getNamaFileGambar();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
        
        try{
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),StandardCopyOption.REPLACE_EXISTING);
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
        product.setDiskon(product.getDiskon());
        product.setNamaFileGambar(storageFileName);

        repo.save(product);
        
        return "redirect:/product"; 
    }
    
    
    @GetMapping("/edit")
    public String showEditPage (Model model,@RequestParam int id){
        try{
            Product product = repo.findById(id).get();
            model.addAttribute("product",product);
            
            ProductDTO productDTO = new ProductDTO();
            List<String> categories = repo.findDistinctCategories();
            
            productDTO.setNama(product.getNama());
            productDTO.setDeskripsi(product.getDeskripsi());
            productDTO.setHarga(product.getHarga());
            productDTO.setStok(product.getStok());
            productDTO.setDiskon(product.getDiskon());
            
            model.addAttribute("categories",categories);
            model.addAttribute("productDTO", productDTO);
            
        }catch(Exception ex){
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/product";
        }
        
        return "product/EditProduct";
          
    }
    
    
    @PostMapping("/edit")
    public String updateProduct (Model model, @RequestParam int id, @Valid @ModelAttribute ProductDTO productDTO, BindingResult result){

        try{
            Product product = repo.findById(id).get();
            model.addAttribute("product",product);

            if(result.hasErrors()){
                return "product/EditProduct";
            }
            
            if (!productDTO.getNamaFileGambar().isEmpty()){
                // Delete gambar lama
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getNamaFileGambar());

                try{
                    Files.delete(oldImagePath);
                }catch(Exception ex){
                    System.out.println("Exception: "+ ex.getMessage());
                }

                // Save gambar baru
                MultipartFile image = productDTO.getNamaFileGambar();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()){
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),StandardCopyOption.REPLACE_EXISTING);
                }

                product.setNamaFileGambar(storageFileName);
            }
            product.setNama(productDTO.getNama());
            product.setKategori(productDTO.getKategori());
            product.setDeskripsi(productDTO.getDeskripsi());
            product.setHarga(productDTO.getHarga());
            product.setStok(productDTO.getStok());
            product.setDiskon(productDTO.getDiskon());

            repo.save(product);
        }
        catch(Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/product";
    }


    
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
    try {
        Product product = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getNamaFileGambar()!= null && !product.getNamaFileGambar().isEmpty()) {
            Path imagePath = Paths.get("public/images/" + product.getNamaFileGambar());
            try {
                Files.deleteIfExists(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception deleting image: " + ex.getMessage());
            }
        }
        repo.deleteById(id);

    } catch (Exception ex) {
        System.out.println("Exception: " + ex.getMessage());
    }

    return "redirect:/product";
    }
}
