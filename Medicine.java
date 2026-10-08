package Pharmacy.Management.System.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "medicine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicine
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medId;

    @Column(nullable = false)
    private String medName;

    private String category;

    private String manufacturer;

    private String description;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @JsonManagedReference
    @OneToOne(mappedBy = "medicine", cascade = CascadeType.ALL)
    private Inventory inventory;
}