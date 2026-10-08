package Pharmacy.Management.System.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(?:\\+94|0)7[0-9]{8}$",
            message = "Enter a valid Sri Lankan mobile number, e.g. 0771234567 or +94771234567"
    )
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^_\\-]).{8,}$",
            message = "Password must be at least 8 characters and include upper, lower, a digit and a symbol"
    )
    private String password;
}
