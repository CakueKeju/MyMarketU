package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.model.ProductDTO;
import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.repository.ProductRepository;
import com.cakwe.MyMarketU.repository.UserRepository;
import com.cakwe.MyMarketU.service.UserService; // Tambahkan ini
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/product")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    private ProductRepository repo;
    
    @Autowired 
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService; // Tambahkan ini
   
    @GetMapping("/list")
    public String showProductList(Model model) {
        try {
            // Ambil data user untuk header
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("user", currentUser); // Tambahkan full user object
                model.addAttribute("userName", currentUser.getNamaLengkap());
                logger.debug("Foto profil user: {}", currentUser.getFotoProfil());
            }
            
            // Ambil semua produk
            List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
            model.addAttribute("products", products);
            
            return "admin/products-admin";
        } catch (Exception e) {
            logger.error("Error loading products page: ", e);
            model.addAttribute("errorMessage", "Gagal memuat halaman produk");
            return "error";
        }
    }
   
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        try {
            // Ambil data user untuk header
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("user", currentUser); // Tambahkan full user object
                model.addAttribute("userName", currentUser.getNamaLengkap());
            }
            
            model.addAttribute("product", new Product());
            return "admin/add-product";
        } catch (Exception e) {
            logger.error("Error showing add product form: ", e);
            model.addAttribute("errorMessage", "Gagal memuat form tambah produk");
            return "error";
        }
    }
   
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute ProductDTO productDTO,
                           BindingResult result,
                           @RequestParam("imageFile") MultipartFile file,
                           Model model) {
        try {
            // Tambahkan data user untuk header jika terjadi error
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("user", currentUser);
                model.addAttribute("userName", currentUser.getNamaLengkap());
            }
            
            if(productDTO.getHarga() < 0 || productDTO.getStok() < 0 || productDTO.getDiskon() < 0) {
                model.addAttribute("error", "Harga, stok, dan diskon tidak boleh negatif");
                return "admin/add-product";
            }
            
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
            return "redirect:/admin/product/list";
            
        } catch (Exception e) {
            logger.error("Error adding product: ", e);
            model.addAttribute("error", "Gagal menambahkan produk");
            return "admin/add-product";
        }
    }
   
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        try {
            // Ambil data user untuk header
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("user", currentUser);
                model.addAttribute("userName", currentUser.getNamaLengkap());
            }
            
            Product product = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            model.addAttribute("product", product);
            return "admin/edit-product";
            
        } catch (Exception e) {
            logger.error("Error showing edit form: ", e);
            return "redirect:/admin/product/list";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") Integer id,
                              @Valid @ModelAttribute ProductDTO productDTO,
                              BindingResult result,
                              @RequestParam("imageFile") MultipartFile file,
                              Model model) {
        try {
            // Tambahkan data user untuk header jika terjadi error
            User currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("user", currentUser);
                model.addAttribute("userName", currentUser.getNamaLengkap());
            }
            
            if(productDTO.getHarga() < 0 || productDTO.getStok() < 0 || productDTO.getDiskon() < 0) {
                model.addAttribute("error", "Harga, stok, dan diskon tidak boleh negatif");
                return "admin/edit-product";
            }
            
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
            return "redirect:/admin/product/list";
            
        } catch (Exception e) {
            logger.error("Error updating product: ", e);
            model.addAttribute("error", "Gagal mengupdate produk");
            return "admin/edit-product";
        }
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
            logger.error("Error deleting product: ", e);
        }
        
        return "redirect:/admin/product/list";
    }
}