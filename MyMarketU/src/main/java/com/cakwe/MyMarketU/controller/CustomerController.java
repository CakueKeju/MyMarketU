package com.cakwe.MyMarketU.controller;

import com.cakwe.MyMarketU.model.User;
import com.cakwe.MyMarketU.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class CustomerController {

   @Autowired
   private UserService userService;
   
   @GetMapping("/customersmenu-admin")
   public String showCustomerMenu(Model model) {
       List<User> customers = userService.getAllCustomers();
       
       model.addAttribute("customers", customers);
       model.addAttribute("totalCustomers", customers.size());
       model.addAttribute("newCustomersThisMonth", userService.getNewCustomersThisMonth());
       model.addAttribute("activeCustomers", userService.getActiveCustomers());
       
       return "admin/customersmenu-admin";
   }

   @PostMapping("/customers/{id}/block") 
   public String blockCustomer(@PathVariable Long id) {
       userService.updateCustomerStatus(id, "Blocked");
       return "redirect:/admin/customersmenu-admin";
   }

   @GetMapping("/customers/search")
   public String searchCustomers(@RequestParam String query, Model model) {
       List<User> searchResults = userService.searchCustomers(query);
       model.addAttribute("customers", searchResults);
       return "admin/customersmenu-admin :: customerTable"; 
   }

   @GetMapping("/customers/filter")
   public String filterCustomers(
           @RequestParam String status,
           @RequestParam String sortBy, 
           Model model) {
       List<User> filteredCustomers = userService.filterCustomers(status, sortBy);
       model.addAttribute("customers", filteredCustomers);
       return "admin/customersmenu-admin :: customerTable";
   }

   @GetMapping("/customers/{id}")
   public String viewCustomerDetails(@PathVariable Long id, Model model) {
       User customer = userService.getCustomerById(id);
       model.addAttribute("customer", customer);
       return "admin/customer-details";
   }

   @PostMapping("/customers/{id}/edit")
   public String updateCustomer(@PathVariable Long id, @ModelAttribute User customer) {
       userService.updateCustomer(id, customer);
       return "redirect:/admin/customersmenu-admin";
   }
}