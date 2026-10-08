package Pharmacy.Management.System.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ==========================================
    // BASIC USER DETAILS
    // ==========================================

    @Column(
            name = "full_name",
            nullable = false,
            length = 120
    )
    private String fullName;


    @Column(
            nullable = false,
            length = 150
    )
    private String email;


    @Column(
            name = "phone_number",
            nullable = false,
            length = 20
    )
    private String phoneNumber;


    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;


    // ==========================================
    // ROLE
    // ==========================================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "role_id",
            nullable = false
    )
    private Role role;


    // ==========================================
    // EMAIL VERIFICATION
    // ==========================================

    @Column(
            name = "email_verified",
            nullable = false
    )
    @Builder.Default
    private boolean emailVerified = false;


    @Column(
            name = "verification_code",
            length = 6
    )
    private String verificationCode;


    @Column(
            name = "verification_code_expiry"
    )
    private LocalDateTime verificationCodeExpiry;


    // ==========================================
    // PHONE VERIFICATION
    // ==========================================

    @Column(
            name = "phone_verified",
            nullable = false
    )
    @Builder.Default
    private boolean phoneVerified = false;


    // ==========================================
    // USER STATUS
    // ==========================================

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;


    // ==========================================
    // DATE / TIME
    // ==========================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // ==========================================
    // AUTOMATIC CREATED DATE
    // ==========================================

    @PrePersist
    protected void onCreate() {

        this.createdAt =
                LocalDateTime.now();

        this.updatedAt =
                LocalDateTime.now();
    }


    // ==========================================
    // AUTOMATIC UPDATED DATE
    // ==========================================

    @PreUpdate
    protected void onUpdate() {

        this.updatedAt =
                LocalDateTime.now();
    }
}