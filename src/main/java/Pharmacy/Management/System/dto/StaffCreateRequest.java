package Pharmacy.Management.System.dto;

import Pharmacy.Management.System.entity.RoleName;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffCreateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 120)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(?:\\+94|0)7[0-9]{8}$",
            message = "Enter a valid Sri Lankan mobile number"
    )
    private String phoneNumber;

    @NotBlank(message = "Temporary password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "Role is required")
    private RoleName role;
}
