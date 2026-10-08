package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long>
{

}
