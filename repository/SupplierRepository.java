package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}