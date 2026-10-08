package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.model.Medicine;
import Pharmacy.Management.System.service.MedicineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController
{
    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService)
    {
        this.medicineService = medicineService;
    }

    @GetMapping
    public List<Medicine> getAllMedicine()
    {
        return medicineService.getAllMedicines();
    }

    @GetMapping("/{id}")
    public Medicine getMedicineById(@PathVariable Long id)
    {
        return medicineService.getMedicinesById(id).orElse(null);
    }

    @PostMapping
    public Medicine createMedicine(@RequestBody Medicine medicine)
    {
        return medicineService.saveMedicine(medicine);
    }

    @PutMapping("/{id}")
    public Medicine updateMedicine(@PathVariable Long id, @RequestBody Medicine medicine)
    {
        return medicineService.updateMedicine(id, medicine);
    }

    @DeleteMapping("/{id}")
    public void deleteMedicine(@PathVariable Long id)
    {
        medicineService.deleteMedicine(id);
    }

}
