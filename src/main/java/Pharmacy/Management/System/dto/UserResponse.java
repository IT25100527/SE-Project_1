package Pharmacy.Management.System.dto;

import Pharmacy.Management.System.entity.RoleName;
import Pharmacy.Management.System.entity.User;
import Pharmacy.Management.System.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private RoleName role;
    private boolean emailVerified;
    private boolean phoneVerified;
    private UserStatus status;
    private LocalDateTime createdAt;

    public static UserResponse from(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .role(u.getRole().getName())
                .emailVerified(u.isEmailVerified())
                .phoneVerified(u.isPhoneVerified())
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
