package Pharmacy.Management.System.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
