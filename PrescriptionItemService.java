package Pharmacy.Management.System.service;

import Pharmacy.Management.System.model.Medicine;
import Pharmacy.Management.System.model.Prescription;
import Pharmacy.Management.System.model.PrescriptionItem;
import Pharmacy.Management.System.repository.MedicineRepository;
import Pharmacy.Management.System.repository.PrescriptionItemRepository;
import Pharmacy.Management.System.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionItemService
{
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionItemService(
            PrescriptionItemRepository prescriptionItemRepository,
            MedicineRepository medicineRepository,
            PrescriptionRepository prescriptionRepository)
    {
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.medicineRepository = medicineRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<PrescriptionItem> getAllPrescriptionItems()
    {
        return prescriptionItemRepository.findAll();
    }

    public Optional<PrescriptionItem> getPrescriptionItemById(Long id)
    {
        return prescriptionItemRepository.findById(id);
    }

    public PrescriptionItem savePrescriptionItem(PrescriptionItem prescriptionItem)
    {
        Long medicineId = prescriptionItem.getMedicine().getMedId();
        Long prescriptionId = prescriptionItem.getPrescription().getPresId();

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescriptionItem.setMedicine(medicine);
        prescriptionItem.setPrescription(prescription);

        return prescriptionItemRepository.save(prescriptionItem);
    }

    public PrescriptionItem updatePrescriptionItem(
            Long id,
            PrescriptionItem prescriptionItemDetails)
    {
        PrescriptionItem prescriptionItem = prescriptionItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription item not found"));

        prescriptionItem.setQty(prescriptionItemDetails.getQty());
        prescriptionItem.setDosage(prescriptionItemDetails.getDosage());
        prescriptionItem.setInstructions(prescriptionItemDetails.getInstructions());

        if (prescriptionItemDetails.getMedicine() != null)
        {
            Long medicineId = prescriptionItemDetails.getMedicine().getMedId();

            Medicine medicine = medicineRepository.findById(medicineId)
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));

            prescriptionItem.setMedicine(medicine);
        }

        if (prescriptionItemDetails.getPrescription() != null)
        {
            Long prescriptionId = prescriptionItemDetails.getPrescription().getPresId();

            Prescription prescription = prescriptionRepository.findById(prescriptionId)
                    .orElseThrow(() -> new RuntimeException("Prescription not found"));

            prescriptionItem.setPrescription(prescription);
        }

        return prescriptionItemRepository.save(prescriptionItem);
    }

    public void deletePrescriptionItem(Long id)
    {
        prescriptionItemRepository.deleteById(id);
    }
}