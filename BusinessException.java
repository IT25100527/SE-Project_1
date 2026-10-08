package com.pharmacy.sales.exception;

/** Rule violations such as insufficient stock, expired medicine or overpayment. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
