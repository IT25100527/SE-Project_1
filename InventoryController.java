package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.model.Inventory;
import Pharmacy.Management.System.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController
{
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService)
    {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Inventory> getAllInventory()
    {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/{id}")
    public Inventory getInventoryById(@PathVariable Long id)
    {
        return inventoryService.getInventoryById(id).orElse(null);
    }

    @PostMapping
    public Inventory createInventory(@RequestBody Inventory inventory)
    {
        return inventoryService.saveInventory(inventory);
    }

    @PutMapping("/{id}")
    public Inventory updateInventory(@PathVariable Long id, @RequestBody Inventory inventory)
    {
        return inventoryService.updateInventory(id, inventory);
    }

    @DeleteMapping("/{id}")
    public void deleteInventory(@PathVariable Long id)
    {
        inventoryService.deleteInventory(id);
    }

}
