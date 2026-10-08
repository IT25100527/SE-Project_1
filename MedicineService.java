package com.pharmacy.sales.service;

import com.pharmacy.sales.exception.ResourceNotFoundException;
import com.pharmacy.sales.model.Medicine;
import com.pharmacy.sales.repository.MedicineRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository repo;

    public MedicineService(MedicineRepository repo) { this.repo = repo; }

    public List<Medicine> search(String q) {
        if (q == null || q.isBlank()) return repo.findAll(Sort.by("name"));
        return repo.findByNameContainingIgnoreCaseOrderByNameAsc(q.trim());
    }

    public Medicine get(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Medicine " + id + " not found"));
    }

    @Transactional
    public Medicine create(Medicine m) {
        m.setId(null);
        return repo.save(m);
    }

    @Transactional
    public Medicine update(Long id, Medicine in) {
        Medicine m = get(id);
        m.setName(in.getName());
        m.setBatchNo(in.getBatchNo());
        m.setCategory(in.getCategory());
        m.setUnitPrice(in.getUnitPrice());
        m.setQuantityInStock(in.getQuantityInStock());
        m.setExpiryDate(in.getExpiryDate());
        m.setReorderLevel(in.getReorderLevel());
        return repo.save(m);
    }

    @Transactional
    public void delete(Long id) {
        repo.delete(get(id));
    }
}
