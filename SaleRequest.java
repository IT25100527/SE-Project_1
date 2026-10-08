package com.pharmacy.sales.dto;

import com.pharmacy.sales.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record SaleRequest(
        String customerName,
        String customerPhone,
        @NotEmpty(message = "Add at least one medicine to the sale") @Valid List<SaleItemRequest> items,
        @DecimalMin(value = "0", message = "Discount cannot be negative") BigDecimal discount,
        @DecimalMin(value = "0", message = "Amount paid cannot be negative") BigDecimal amountPaid,
        @NotNull(message = "Payment method is required") PaymentMethod paymentMethod) {
}
