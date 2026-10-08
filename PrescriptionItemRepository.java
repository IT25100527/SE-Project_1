package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.model.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long>
{

}
