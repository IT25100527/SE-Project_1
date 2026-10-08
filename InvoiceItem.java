package com.pharmacy.sales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** A sold line. Name/price are copied from the medicine so old invoices never change. */
@Entity
@Table(name = "invoice_items")
@Getter @Setter @NoArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    private Long medicineId;
    private String medicineName;
    private String batchNo;

    @Column(precision = 12, scale = 2) private BigDecimal unitPrice;
    private int quantity;
    @Column(precision = 12, scale = 2) private BigDecimal lineTotal;
}
