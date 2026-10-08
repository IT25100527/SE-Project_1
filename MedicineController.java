package com.pharmacy.sales.controller;

import com.pharmacy.sales.model.Medicine;
import com.pharmacy.sales.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService service;

    public MedicineController(MedicineService service) { this.service = service; }

    @GetMapping
    public List<Medicine> list(@RequestParam(required = false) String search) {
        return service.search(search);
    }

    @GetMapping("/{id}")
    public Medicine get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medicine create(@Valid @RequestBody Medicine medicine) { return service.create(medicine); }

    @PutMapping("/{id}")
    public Medicine update(@PathVariable Long id, @Valid @RequestBody Medicine medicine) {
        return service.update(id, medicine);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
