package Pharmacy.Management.System.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long presItemId;

    @Column(nullable = false)
    private Integer qty;

    private String dosage;

    private String instructions;

    @ManyToOne
    @JoinColumn(name = "med_id", nullable = false)
    private Medicine medicine;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "pres_id", nullable = false)
    private Prescription prescription;
}