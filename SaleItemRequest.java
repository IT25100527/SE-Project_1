package com.pharmacy.sales.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SaleItemRequest(
        @NotNull(message = "Medicine is required") Long medicineId,
        @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
}
