package com.pharmacy.sales.controller;

import com.pharmacy.sales.dto.PaymentRequest;
import com.pharmacy.sales.dto.SaleRequest;
import com.pharmacy.sales.model.Invoice;
import com.pharmacy.sales.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final SalesService service;

    public InvoiceController(SalesService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Invoice createSale(@Valid @RequestBody SaleRequest request) {
        return service.createSale(request);
    }

    @GetMapping
    public List<Invoice> list() { return service.listInvoices(); }

    @GetMapping("/{id}")
    public Invoice get(@PathVariable Long id) { return service.getInvoice(id); }

    @PostMapping("/{id}/payments")
    public Invoice addPayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        return service.addPayment(id, request);
    }
}
