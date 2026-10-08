package Pharmacy.Management.System.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Supplier entity — matches the fields shown in the "Suppliers Directory"
 * table, the "View Details" modal, and the "Add / Edit Supplier" form of
 * the Rx+ PHARMACORE frontend (src/main/resources/static).
 * <p>
 * NOTE: No Lombok is used here on purpose (plain getters/setters) so the
 * project compiles even if annotation processing is not enabled in the IDE.
 */
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Human-friendly code shown in the modal, e.g. "SUP-00124" (auto-generated after insert)
    @Column(name = "supplier_code", unique = true, length = 30)
    private String supplierCode;

    @NotBlank(message = "Supplier name is required")
    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    @NotBlank(message = "Contact person is required")
    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    // e.g. "Sales Manager" - shown in the view modal
    @Column(name = "designation")
    private String designation;

    @NotBlank(message = "Phone is required")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierStatus status = SupplierStatus.ACTIVE;

    // Business Registration Number (BRN)
    @Column(name = "business_reg_no")
    private String businessRegNo;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Supplier() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = SupplierStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---------- Getters & Setters ----------

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

    public SupplierStatus getStatus() {
        return status;
    }

    public void setStatus(SupplierStatus status) {
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

    public enum SupplierStatus {
        ACTIVE, INACTIVE
    }
}
