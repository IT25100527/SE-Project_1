package Pharmacy.Management.System.dto;

import Pharmacy.Management.System.entity.RoleName;
import Pharmacy.Management.System.entity.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @Size(min = 2, max = 120)
    private String fullName;

    @Email
    private String email;

    @Pattern(regexp = "^(?:\\+94|0)7[0-9]{8}$", message = "Enter a valid Sri Lankan mobile number")
    private String phoneNumber;

    private RoleName role;

    private UserStatus status;
}
