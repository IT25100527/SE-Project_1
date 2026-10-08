package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long>
{

}
