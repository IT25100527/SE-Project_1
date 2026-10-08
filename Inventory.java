package Pharmacy.Management.System.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @Column(name = "qty_avaialble", nullable = false)
    private Integer qtyAvaialble;

    private Integer reorderLvl;

    private String stockStat;

    private LocalDate manDate;

    private LocalDate expDate;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "medId", nullable = false, unique = true)
    private Medicine medicine;
}