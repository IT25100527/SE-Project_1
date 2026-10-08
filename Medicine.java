package com.pharmacy.sales.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/** READ-ONLY view of the medicine catalogue owned by another team member (table: medicine). */
@Entity
@Immutable
@Table(name = "medicine")
@Getter @Setter @NoArgsConstructor
public class Medicine {

    @Id
    @Column(name = "med_id")
    private Long id;

    @Column(name = "med_name")
    private String name;

    private String category;
    private String manufacturer;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;
}