package com.pharmacy.sales.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter @Setter @NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String invoiceNumber;

    private String customerName;
    private String customerPhone;
    private LocalDateTime invoiceDate;

    @Column(precision = 12, scale = 2) private BigDecimal subtotal = BigDecimal.ZERO;
    @Column(precision = 12, scale = 2) private BigDecimal discount = BigDecimal.ZERO;
    @Column(precision = 12, scale = 2) private BigDecimal totalAmount = BigDecimal.ZERO;
    @Column(precision = 12, scale = 2) private BigDecimal paidAmount = BigDecimal.ZERO;
    @Column(precision = 12, scale = 2) private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @OrderBy("paidAt ASC")
    private List<Payment> payments = new ArrayList<>();

    public void addItem(InvoiceItem item) {
        item.setInvoice(this);
        items.add(item);
    }

    public void addPayment(Payment payment) {
        payment.setInvoice(this);
        payments.add(payment);
    }
}
