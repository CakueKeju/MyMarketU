package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.model.ProductDTO;
import com.cakwe.MyMarketU.repository.ProductRepository;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/product")
public class ProductController {
    @Autowired
    private ProductRepository repo;
    
    @GetMapping("/list")
    public String showProductList(Model model) {
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "admin/products-admin";
    }
    
    @PostMapping("/add")
    public String addProduct(@ModelAttribute ProductDTO productDTO, 
                           @RequestParam("imageFile") MultipartFile file) {
        try {
            Product product = new Product();
            product.setNama(productDTO.getNama());
            product.setKategori(productDTO.getKategori());
            product.setDeskripsi(productDTO.getDeskripsi());
            product.setHarga(productDTO.getHarga());
            product.setStok(productDTO.getStok());
            product.setDiskon(productDTO.getDiskon());
            
            // Handle file upload
            if (!file.isEmpty()) {
                String uploadDir = "public/images/";
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + fileName), 
                              StandardCopyOption.REPLACE_EXISTING);
                }
                
                product.setNamaFileGambar(fileName);
            }
            
            repo.save(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/product/list";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        try {
            Product product = repo.findById(id).orElse(null);
            if (product != null && product.getNamaFileGambar() != null) {
                Path imagePath = Paths.get("public/images/" + product.getNamaFileGambar());
                Files.deleteIfExists(imagePath);
            }
            repo.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/product/list";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        try {
            Product product = repo.findById(id).orElse(null);
            if (product != null) {
                model.addAttribute("product", product);
                return "admin/edit-product";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/product/list";
    }
    
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") Integer id,
                              @ModelAttribute ProductDTO productDTO,
                              @RequestParam("imageFile") MultipartFile file) {
        try {
            Product product = repo.findById(id).orElse(null);
            if (product != null) {
                product.setNama(productDTO.getNama());
                product.setKategori(productDTO.getKategori());
                product.setDeskripsi(productDTO.getDeskripsi());
                product.setHarga(productDTO.getHarga());
                product.setStok(productDTO.getStok());
                product.setDiskon(productDTO.getDiskon());
                
                if (!file.isEmpty()) {
                    // Delete old image if exists
                    if (product.getNamaFileGambar() != null) {
                        Path oldImagePath = Paths.get("public/images/" + product.getNamaFileGambar());
                        Files.deleteIfExists(oldImagePath);
                    }
                    
                    // Save new image
                    String uploadDir = "public/images/";
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    
                    try (InputStream inputStream = file.getInputStream()) {
                        Files.copy(inputStream, Paths.get(uploadDir + fileName), 
                                  StandardCopyOption.REPLACE_EXISTING);
                    }
                    
                    product.setNamaFileGambar(fileName);
                }
                
                repo.save(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/product/list";
    }
}