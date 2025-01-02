/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cakwe.MyMarketU.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Cakue
 */
public class OrderDTO {
    private int id;
    private String invoiceNumber;
    private double totalPrice;
    private String status;
    private Date orderDate;
    private String orderEvidence;
    private List<OrderItemDTO> orderItems;

    public OrderDTO(int id, String invoiceNumber, double totalPrice, String status, Date orderDate, String orderEvidence, List<OrderItemDTO> orderItems) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderDate = orderDate;
        this.orderEvidence = orderEvidence;
        this.orderItems = orderItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderEvidence() {
        return orderEvidence;
    }

    public void setOrderEvidence(String orderEvidence) {
        this.orderEvidence = orderEvidence;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
    
    
}
