package com.pharmacy.sales.repository;

import com.pharmacy.sales.model.Medicine;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    /** Row lock so two cashiers can't sell the same last strip at the same time. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Medicine m where m.id = :id")
    Optional<Medicine> findByIdForUpdate(@Param("id") Long id);
}
