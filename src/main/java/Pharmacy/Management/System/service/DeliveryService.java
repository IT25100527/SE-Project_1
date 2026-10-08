package Pharmacy.Management.System.delivery.service;

import Pharmacy.Management.System.delivery.entity.Delivery;
import Pharmacy.Management.System.delivery.exception.DeliveryNotFoundException;
import Pharmacy.Management.System.delivery.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    // CREATE
    public Delivery createDelivery(Delivery delivery) {

        if (delivery.getStatus() == null ||
                delivery.getStatus().isEmpty()) {

            delivery.setStatus("PENDING");
        }

        return deliveryRepository.save(delivery);
    }

    // READ ALL
    public List<Delivery> getAllDeliveries() {

        return deliveryRepository.findAll();
    }

    // READ ONE
    public Optional<Delivery> getDeliveryById(Long id) {

        return deliveryRepository.findById(id);
    }

    // UPDATE
    public Delivery updateDelivery(
            Long id,
            Delivery updatedDelivery) {

        Delivery existingDelivery = findOrThrow(id);

        existingDelivery.setOrderId(
                updatedDelivery.getOrderId());

        existingDelivery.setCustomerName(
                updatedDelivery.getCustomerName());

        existingDelivery.setDeliveryAddress(
                updatedDelivery.getDeliveryAddress());

        existingDelivery.setPhoneNumber(
                updatedDelivery.getPhoneNumber());

        existingDelivery.setDeliveryPerson(
                updatedDelivery.getDeliveryPerson());

        existingDelivery.setDeliveryDate(
                updatedDelivery.getDeliveryDate());

        existingDelivery.setStatus(
                updatedDelivery.getStatus());

        // NEW: keep totalAmount in sync on update too
        existingDelivery.setTotalAmount(
                updatedDelivery.getTotalAmount());

        return deliveryRepository.save(existingDelivery);
    }

    // DELETE
    public void deleteDelivery(Long id) {

        if (!deliveryRepository.existsById(id)) {

            throw new DeliveryNotFoundException(id);
        }

        deliveryRepository.deleteById(id);
    }

    // UPDATE STATUS
    public Delivery updateStatus(
            Long id,
            String status) {

        Delivery delivery = findOrThrow(id);

        delivery.setStatus(status);

        // NEW: stamp the completion time whenever it's marked delivered
        if ("DELIVERED".equalsIgnoreCase(status)) {
            delivery.setDeliveredAt(LocalDateTime.now());
        }

        return deliveryRepository.save(delivery);
    }

    // NEW: assign / reassign a delivery to a delivery person (PBI: "assign
    // deliveries to delivery staff" from the proposal's core functions)
    public Delivery assignDeliveryPerson(Long id, String deliveryPerson) {

        Delivery delivery = findOrThrow(id);

        delivery.setDeliveryPerson(deliveryPerson);

        if (delivery.getStatus() == null
                || "PENDING".equalsIgnoreCase(delivery.getStatus())) {
            delivery.setStatus("OUT_FOR_DELIVERY");
        }

        return deliveryRepository.save(delivery);
    }

    // NEW: capture proof of delivery (PBI-19). Called once the delivery
    // person confirms drop-off — takes a reference to an uploaded image/
    // signature or a text confirmation note.
    public Delivery captureProofOfDelivery(Long id, String proofOfDelivery) {

        Delivery delivery = findOrThrow(id);

        delivery.setProofOfDelivery(proofOfDelivery);
        delivery.setStatus("DELIVERED");
        delivery.setDeliveredAt(LocalDateTime.now());

        return deliveryRepository.save(delivery);
    }

    // NEW: deliveries assigned to a specific delivery person (PBI-17)
    public List<Delivery> getDeliveriesByPerson(String deliveryPerson) {

        return deliveryRepository.findByDeliveryPerson(deliveryPerson);
    }

    // NEW: filter by status
    public List<Delivery> getDeliveriesByStatus(String status) {

        return deliveryRepository.findByStatus(status);
    }

    // NEW: deliveries for a given order
    public List<Delivery> getDeliveriesByOrderId(Long orderId) {

        return deliveryRepository.findByOrderId(orderId);
    }

    // NEW: search by customer name (partial match)
    public List<Delivery> searchByCustomerName(String customerName) {

        return deliveryRepository.findByCustomerNameContainingIgnoreCase(customerName);
    }

    // Shared lookup-or-throw helper used across the methods above
    private Delivery findOrThrow(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
    }
}
