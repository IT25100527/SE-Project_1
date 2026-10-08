package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.model.Prescription;
import Pharmacy.Management.System.service.PrescriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public List<Prescription> getAllPrescriptions()
    {
        return prescriptionService.getAllPrescriptions();
    }

    @GetMapping("/{id}")
    public Prescription getPrescriptionById(@PathVariable Long id)
    {
        return prescriptionService.getPrescriptionsById(id).orElse(null);
    }

    @PostMapping
    public Prescription createPrescription(@RequestBody Prescription prescription)
    {
        return prescriptionService.savePrescriptions(prescription);
    }

    @PutMapping("/{id}")
    public Prescription updatePrescription(@PathVariable Long id, @RequestBody Prescription prescription)
    {
        return prescriptionService.updatePrescriptions(id, prescription);
    }

    @DeleteMapping("/{id}")
    public void deletePrescription(@PathVariable Long id)
    {
        prescriptionService.deletePrescription(id);
    }

}
