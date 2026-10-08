package Pharmacy.Management.System.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "permission_name",
            nullable = false,
            unique = true,
            length = 100
    )
    private String permissionName;

    @Column(
            nullable = false,
            length = 80
    )
    private String name;

    @Column(length = 255)
    private String description;
}