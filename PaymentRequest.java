package com.pharmacy.sales.dto;

import com.pharmacy.sales.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0") BigDecimal amount,
        @NotNull(message = "Payment method is required") PaymentMethod method) {
}
