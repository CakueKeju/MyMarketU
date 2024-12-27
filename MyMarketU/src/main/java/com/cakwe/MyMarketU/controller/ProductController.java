package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.model.ProductDTO;
import com.cakwe.MyMarketU.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
   
   @GetMapping("/add")
   public String showAddProductForm(Model model) {
       model.addAttribute("product", new Product());
       return "admin/add-product";
   }
   
   @PostMapping("/add")
   public String addProduct(@Valid @ModelAttribute ProductDTO productDTO,
                          BindingResult result,
                          @RequestParam("imageFile") MultipartFile file,
                          Model model) {
       
       if(productDTO.getHarga() < 0 || productDTO.getStok() < 0 || productDTO.getDiskon() < 0) {
           model.addAttribute("error", "Harga, stok, dan diskon tidak boleh negatif");
           return "admin/add-product";
       }
       
       try {
           Product product = new Product();
           product.setNama(productDTO.getNama());
           product.setKategori(productDTO.getKategori());
           product.setDeskripsi(productDTO.getDeskripsi());
           product.setHarga(productDTO.getHarga());
           product.setStok(productDTO.getStok());
           product.setDiskon(productDTO.getDiskon());
           
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
           model.addAttribute("error", "Gagal menambahkan produk");
           return "admin/add-product";
       }
       
       return "redirect:/admin/product/list";
   }
   
   @GetMapping("/edit/{id}")
   public String showEditForm(@PathVariable("id") Integer id, Model model) {
       try {
           Product product = repo.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
           model.addAttribute("product", product);
           return "admin/edit-product";
       } catch (Exception e) {
           e.printStackTrace();
           return "redirect:/admin/product/list";
       }
   }

   @PostMapping("/edit/{id}")
   public String updateProduct(@PathVariable("id") Integer id,
                             @Valid @ModelAttribute ProductDTO productDTO,
                             BindingResult result,
                             @RequestParam("imageFile") MultipartFile file,
                             Model model) {
                                 
       if(productDTO.getHarga() < 0 || productDTO.getStok() < 0 || productDTO.getDiskon() < 0) {
           model.addAttribute("error", "Harga, stok, dan diskon tidak boleh negatif");
           return "admin/edit-product";
       }
       
       try {
           Product product = repo.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

           product.setNama(productDTO.getNama());
           product.setKategori(productDTO.getKategori());
           product.setDeskripsi(productDTO.getDeskripsi());
           product.setHarga(productDTO.getHarga());
           product.setStok(productDTO.getStok());
           product.setDiskon(productDTO.getDiskon());

           if (!file.isEmpty()) {
               if (product.getNamaFileGambar() != null) {
                   Path oldImagePath = Paths.get("public/images/" + product.getNamaFileGambar());
                   Files.deleteIfExists(oldImagePath);
               }

               String uploadDir = "public/images/";
               String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

               try (InputStream inputStream = file.getInputStream()) {
                   Files.copy(inputStream, Paths.get(uploadDir + fileName),
                             StandardCopyOption.REPLACE_EXISTING);
               }

               product.setNamaFileGambar(fileName);
           }

           repo.save(product);
       } catch (Exception e) {
           e.printStackTrace();
           model.addAttribute("error", "Gagal mengupdate produk");
           return "admin/edit-product";
       }

       return "redirect:/admin/product/list";
   }

   @PostMapping("/delete/{id}")
   public String deleteProduct(@PathVariable("id") Integer id) {
       try {
           Product product = repo.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
                   
           if (product.getNamaFileGambar() != null) {
               Path imagePath = Paths.get("public/images/" + product.getNamaFileGambar());
               Files.deleteIfExists(imagePath);
           }
           
           repo.delete(product);
       } catch (Exception e) {
           e.printStackTrace();
       }
       
       return "redirect:/admin/product/list";
   }
}