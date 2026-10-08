package Pharmacy.Management.System.service;

import Pharmacy.Management.System.dto.SupplierRequest;
import Pharmacy.Management.System.dto.SupplierResponse;
import Pharmacy.Management.System.entity.Supplier;
import Pharmacy.Management.System.exception.ResourceNotFoundException;
import Pharmacy.Management.System.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(SupplierResponse::fromEntity)
                .toList();
    }

    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = findSupplierOrThrow(id);
        return SupplierResponse.fromEntity(supplier);
    }

    public SupplierResponse createSupplier(SupplierRequest request) {
        Supplier supplier = new Supplier();
        mapRequestToEntity(request, supplier);

        Supplier saved = supplierRepository.save(supplier);

        // Generate a human-friendly code now that the DB has assigned an id,
        // e.g. "SUP-00124" — matches the format shown in the View modal.
        saved.setSupplierCode("SUP-" + String.format("%05d", saved.getId()));
        saved = supplierRepository.save(saved);

        return SupplierResponse.fromEntity(saved);
    }

    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = findSupplierOrThrow(id);
        mapRequestToEntity(request, supplier);
        Supplier updated = supplierRepository.save(supplier);
        return SupplierResponse.fromEntity(updated);
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = findSupplierOrThrow(id);
        supplierRepository.delete(supplier);
    }

    private Supplier findSupplierOrThrow(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
    }

    private void mapRequestToEntity(SupplierRequest request, Supplier supplier) {
        supplier.setSupplierName(request.getSupplierName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setDesignation(request.getDesignation());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setCategory(request.getCategory());
        supplier.setBusinessRegNo(request.getBusinessRegNo());
        supplier.setPaymentTerms(request.getPaymentTerms());
        supplier.setAddress(request.getAddress());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            supplier.setStatus(Supplier.SupplierStatus.valueOf(request.getStatus().toUpperCase()));
        }
    }
}
