package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.model.PrescriptionItem;
import Pharmacy.Management.System.service.PrescriptionItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescription-items")
public class PrescriptionItemController {
    private final PrescriptionItemService prescriptionItemService;

    public PrescriptionItemController(PrescriptionItemService prescriptionItemService)
    {
        this.prescriptionItemService = prescriptionItemService;
    }

    @GetMapping
    public List<PrescriptionItem> getAllPrescriptions()
    {
        return prescriptionItemService.getAllPrescriptionItems();
    }

    @GetMapping("/{id}")
    public PrescriptionItem getPrescriptionItemById(@PathVariable Long id)
    {
        return prescriptionItemService.getPrescriptionItemById(id).orElse(null);
    }

    @PostMapping
    public PrescriptionItem createPrescriptionItem(@RequestBody PrescriptionItem prescriptionItem)
    {
        return prescriptionItemService.savePrescriptionItem(prescriptionItem);
    }

    @PutMapping("/{id}")
    public PrescriptionItem  updatePrescriptionItem(@PathVariable Long id, @RequestBody PrescriptionItem prescriptionItem)
    {
        return prescriptionItemService.updatePrescriptionItem(id, prescriptionItem);
    }

    @DeleteMapping("/{id}")
    public void deletePrescriptionItem(@PathVariable Long id)
    {
        prescriptionItemService.deletePrescriptionItem(id);
    }

}


