package Pharmacy.Management.System.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * What a logged-in user may change about their own account ("My Account").
 * Deliberately excludes role/status - those stay admin-only (see UserUpdateRequest).
 */
@Getter
@Setter
public class SelfProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(?:\\+94|0)7[0-9]{8}$",
            message = "Enter a valid Sri Lankan mobile number, e.g. 0771234567 or +94771234567"
    )
    private String phoneNumber;
}