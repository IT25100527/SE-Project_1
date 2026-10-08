package Pharmacy.Management.System.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "prescription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prescription
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long presId;

    private LocalDate presDate;

    private String docName;

    private String status;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PrescriptionItem> prescriptionItems;
}