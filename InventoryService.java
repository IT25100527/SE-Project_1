package Pharmacy.Management.System.service;

import Pharmacy.Management.System.model.Inventory;
import Pharmacy.Management.System.model.Medicine;
import Pharmacy.Management.System.repository.InventoryRepository;
import Pharmacy.Management.System.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService
{
    private final InventoryRepository inventoryRepository;
    private final MedicineRepository medicineRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            MedicineRepository medicineRepository)
    {
        this.inventoryRepository = inventoryRepository;
        this.medicineRepository = medicineRepository;
    }

    public List<Inventory> getAllInventory()
    {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getInventoryById(Long id)
    {
        return inventoryRepository.findById(id);
    }

    public Inventory saveInventory(Inventory inventory)
    {
        Long medicineId = inventory.getMedicine().getMedId();

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        inventory.setMedicine(medicine);

        return inventoryRepository.save(inventory);
    }

    public Inventory updateInventory(Long id, Inventory inventoryDetails)
    {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setQtyAvaialble(inventoryDetails.getQtyAvaialble());
        inventory.setReorderLvl(inventoryDetails.getReorderLvl());
        inventory.setStockStat(inventoryDetails.getStockStat());
        inventory.setExpDate(inventoryDetails.getExpDate());
        inventory.setManDate(inventoryDetails.getManDate());

        if (inventoryDetails.getMedicine() != null)
        {
            Long medicineId = inventoryDetails.getMedicine().getMedId();

            Medicine medicine = medicineRepository.findById(medicineId)
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));

            inventory.setMedicine(medicine);
        }

        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id)
    {
        inventoryRepository.deleteById(id);
    }
}