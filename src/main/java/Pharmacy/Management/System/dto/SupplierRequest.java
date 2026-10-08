package Pharmacy.Management.System.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Payload sent by the frontend (script.js) when adding or editing a
 * supplier through the "Add / Edit Supplier" form.
 */
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    private String supplierName;

    @NotBlank(message = "Contact person is required")
    private String contactPerson;

    private String designation;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Category is required")
    private String category;

    // "ACTIVE" or "INACTIVE" - optional, defaults to ACTIVE on create
    private String status;

    private String businessRegNo;

    private String paymentTerms;

    private String address;

    public SupplierRequest() {
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
}
