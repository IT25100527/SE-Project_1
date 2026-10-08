package Pharmacy.Management.System.dto;

import Pharmacy.Management.System.entity.Supplier;

import java.time.LocalDateTime;

/**
 * What the frontend (index.html / script.js) receives back from the API.
 * Field names line up 1-to-1 with the table columns, the view modal and
 * the add/edit form.
 */
public class SupplierResponse {

    private Long id;
    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String designation;
    private String phone;
    private String email;
    private String category;
    private String status;
    private String businessRegNo;
    private String paymentTerms;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupplierResponse() {
    }

    public static SupplierResponse fromEntity(Supplier supplier) {
        SupplierResponse response = new SupplierResponse();
        response.setId(supplier.getId());
        response.setSupplierCode(supplier.getSupplierCode());
        response.setSupplierName(supplier.getSupplierName());
        response.setContactPerson(supplier.getContactPerson());
        response.setDesignation(supplier.getDesignation());
        response.setPhone(supplier.getPhone());
        response.setEmail(supplier.getEmail());
        response.setCategory(supplier.getCategory());
        response.setStatus(supplier.getStatus() != null ? supplier.getStatus().name() : null);
        response.setBusinessRegNo(supplier.getBusinessRegNo());
        response.setPaymentTerms(supplier.getPaymentTerms());
        response.setAddress(supplier.getAddress());
        response.setCreatedAt(supplier.getCreatedAt());
        response.setUpdatedAt(supplier.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBusinessRegNo() {
        return businessRegNo;
    }

    public void setBusinessRegNo(String businessRegNo) {
        this.businessRegNo = businessRegNo;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
