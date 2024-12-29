/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.Product;
import com.cakwe.MyMarketU.model.TransactionItemDTO;
import com.cakwe.MyMarketU.service.TransactionService;
import com.cakwe.MyMarketU.repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
/**
 *
 * @author Cakue
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/homepage")
    public String showHomepage(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "customer/homepage";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam String transactionId, @RequestParam int productId, @RequestParam int quantity) {
        TransactionItemDTO itemDTO = new TransactionItemDTO();
        itemDTO.setProductId(productId);
        itemDTO.setQuantity(quantity);
        transactionService.addItemToCart(transactionId, itemDTO);
        return "redirect:/customer/homepage";
    }
}
