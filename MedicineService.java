package Pharmacy.Management.System.service;

import Pharmacy.Management.System.model.Medicine;
import Pharmacy.Management.System.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineService
{
    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository)
    {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> getAllMedicines()
    {
        return medicineRepository.findAll();
    }

    public Optional<Medicine> getMedicinesById(Long id)
    {
        return medicineRepository.findById(id);
    }

    public Medicine saveMedicine(Medicine medicine)
    {
        return medicineRepository.save(medicine);
    }

    public Medicine updateMedicine(Long id, Medicine medicineDetails)
    {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        medicine.setMedName(medicineDetails.getMedName());
        medicine.setCategory(medicineDetails.getCategory());
        medicine.setManufacturer(medicineDetails.getManufacturer());
        medicine.setDescription(medicineDetails.getDescription());
        medicine.setUnitPrice(medicineDetails.getUnitPrice());

        return medicineRepository.save(medicine);
    }

    public void deleteMedicine(Long id) {
        medicineRepository.deleteById(id);
    }
}
