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
public class TransactionDTO {
    private String userId; 
    private String invoiceNumber;
    private double totalCost;
    private String status; 
    private List<TransactionItemDTO> items; 
    private Date transactionDate;
    private Date lastUpdated;
    private String transactionEvidence;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TransactionItemDTO> getItems() {
        return items;
    }

    public void setItems(List<TransactionItemDTO> items) {
        this.items = items;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getTransactionEvidence() {
        return transactionEvidence;
    }

    public void setTransactionEvidence(String transactionEvidence) {
        this.transactionEvidence = transactionEvidence;
    }
    
}
