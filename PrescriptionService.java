package Pharmacy.Management.System.service;

import Pharmacy.Management.System.model.Prescription;
import Pharmacy.Management.System.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService
{
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository)
    {
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<Prescription> getAllPrescriptions()
    {
        return prescriptionRepository.findAll();
    }

    public Optional<Prescription> getPrescriptionsById(Long id)
    {
        return prescriptionRepository.findById(id);
    }

    public Prescription savePrescriptions(Prescription prescription)
    {
        return prescriptionRepository.save(prescription);
    }

    public Prescription updatePrescriptions(Long id, Prescription prescriptionDetails)
    {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.setPresDate(prescriptionDetails.getPresDate());
        prescription.setDocName(prescriptionDetails.getDocName());
        prescription.setStatus(prescriptionDetails.getStatus());

        return prescriptionRepository.save(prescription);
    }

    public void deletePrescription(Long id)
    {
        prescriptionRepository.deleteById(id);
    }

}
