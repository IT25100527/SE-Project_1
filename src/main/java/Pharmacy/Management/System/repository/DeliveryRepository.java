package Pharmacy.Management.System.delivery.repository;

import Pharmacy.Management.System.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // NEW: lets a delivery staff member pull just their own assigned deliveries (PBI-17)
    List<Delivery> findByDeliveryPerson(String deliveryPerson);

    // NEW: filter by status, e.g. all "PENDING" or "DELIVERED" deliveries
    List<Delivery> findByStatus(String status);

    // NEW: look up deliveries tied to a specific order
    List<Delivery> findByOrderId(Long orderId);

    // NEW: search by customer name (partial, case-insensitive) — supports the
    // "search delivery records" requirement from the proposal
    List<Delivery> findByCustomerNameContainingIgnoreCase(String customerName);
}
