package com.pharmacy.sales.service;

import com.pharmacy.sales.dto.PaymentRequest;
import com.pharmacy.sales.dto.SaleItemRequest;
import com.pharmacy.sales.dto.SaleRequest;
import com.pharmacy.sales.exception.BusinessException;
import com.pharmacy.sales.exception.ResourceNotFoundException;
import com.pharmacy.sales.model.*;
import com.pharmacy.sales.repository.InvoiceRepository;
import com.pharmacy.sales.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class SalesService {

    private final InvoiceRepository invoiceRepo;
    private final MedicineRepository medicineRepo;

    public SalesService(InvoiceRepository invoiceRepo, MedicineRepository medicineRepo) {
        this.invoiceRepo = invoiceRepo;
        this.medicineRepo = medicineRepo;
    }

    /**
     * Process a sale in ONE transaction: validate stock -> deduct inventory ->
     * calculate the bill -> create the invoice -> record the payment.
     * If anything fails, the whole thing rolls back (stock is not reduced).
     */
    @Transactional
    public Invoice createSale(SaleRequest req) {
        // Merge duplicate lines; TreeMap locks medicines in id order to avoid deadlocks.
        Map<Long, Integer> qtyByMedicine = new TreeMap<>();
        for (SaleItemRequest line : req.items()) {
            qtyByMedicine.merge(line.medicineId(), line.quantity(), Integer::sum);
        }

        Invoice invoice = new Invoice();
        invoice.setCustomerName(blankToDefault(req.customerName(), "Walk-in customer"));
        invoice.setCustomerPhone(req.customerPhone());
        invoice.setInvoiceDate(LocalDateTime.now());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> e : qtyByMedicine.entrySet()) {
            Medicine m = medicineRepo.findByIdForUpdate(e.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicine " + e.getKey() + " not found"));
            int qty = e.getValue();

            if (m.getExpiryDate().isBefore(LocalDate.now())) {
                throw new BusinessException(m.getName() + " is expired and cannot be sold");
            }
            if (m.getQuantityInStock() < qty) {
                throw new BusinessException("Not enough stock for " + m.getName()
                        + ". Available: " + m.getQuantityInStock());
            }

            m.setQuantityInStock(m.getQuantityInStock() - qty);   // inventory update

            InvoiceItem item = new InvoiceItem();
            item.setMedicineId(m.getId());
            item.setMedicineName(m.getName());
            item.setBatchNo(m.getBatchNo());
            item.setUnitPrice(m.getUnitPrice());
            item.setQuantity(qty);
            item.setLineTotal(m.getUnitPrice().multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP));
            invoice.addItem(item);
            subtotal = subtotal.add(item.getLineTotal());
        }

        BigDecimal discount = money(req.discount());
        if (discount.compareTo(subtotal) > 0) {
            throw new BusinessException("Discount cannot be more than the subtotal");
        }
        BigDecimal total = subtotal.subtract(discount);

        invoice.setSubtotal(subtotal);
        invoice.setDiscount(discount);
        invoice.setTotalAmount(total);

        // Payment received at the counter (cash tendered above the total is treated as change).
        BigDecimal paidNow = money(req.amountPaid()).min(total);
        if (paidNow.signum() > 0) {
            Payment p = new Payment();
            p.setAmount(paidNow);
            p.setMethod(req.paymentMethod());
            p.setPaidAt(LocalDateTime.now());
            invoice.addPayment(p);
        }
        applyTotals(invoice, paidNow);

        Invoice saved = invoiceRepo.save(invoice);
        saved.setInvoiceNumber(String.format("INV-%06d", saved.getId()));
        return invoiceRepo.save(saved);
    }

    /** Record a later payment against an invoice that still has a balance. */
    @Transactional
    public Invoice addPayment(Long invoiceId, PaymentRequest req) {
        Invoice invoice = getInvoice(invoiceId);
        BigDecimal amount = req.amount().setScale(2, RoundingMode.HALF_UP);

        if (invoice.getBalance().signum() == 0) {
            throw new BusinessException("Invoice " + invoice.getInvoiceNumber() + " is already fully paid");
        }
        if (amount.compareTo(invoice.getBalance()) > 0) {
            throw new BusinessException("Payment exceeds the outstanding balance of " + invoice.getBalance());
        }

        Payment p = new Payment();
        p.setAmount(amount);
        p.setMethod(req.method());
        p.setPaidAt(LocalDateTime.now());
        invoice.addPayment(p);

        applyTotals(invoice, invoice.getPaidAmount().add(amount));
        return invoiceRepo.save(invoice);
    }

    @Transactional(readOnly = true)
    public List<Invoice> listInvoices() {
        List<Invoice> list = invoiceRepo.findAllByOrderByInvoiceDateDesc();
        list.forEach(i -> { i.getItems().size(); i.getPayments().size(); }); // initialise lazy lists
        return list;
    }

    @Transactional(readOnly = true)
    public Invoice getInvoice(Long id) {
        Invoice i = invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice " + id + " not found"));
        i.getItems().size();
        i.getPayments().size();
        return i;
    }

    // ---- helpers ----
    private void applyTotals(Invoice invoice, BigDecimal paid) {
        invoice.setPaidAmount(paid);
        invoice.setBalance(invoice.getTotalAmount().subtract(paid));
        if (invoice.getBalance().signum() == 0) invoice.setPaymentStatus(PaymentStatus.PAID);
        else if (paid.signum() == 0) invoice.setPaymentStatus(PaymentStatus.UNPAID);
        else invoice.setPaymentStatus(PaymentStatus.PARTIAL);
    }

    private BigDecimal money(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP);
    }

    private String blankToDefault(String s, String d) {
        return (s == null || s.isBlank()) ? d : s.trim();
    }
}
