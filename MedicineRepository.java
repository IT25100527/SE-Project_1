package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long>
{

}
