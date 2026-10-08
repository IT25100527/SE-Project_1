package Pharmacy.Management.System.delivery.controller;

import Pharmacy.Management.System.delivery.entity.Delivery;
import Pharmacy.Management.System.delivery.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = "*")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(
            DeliveryService deliveryService) {

        this.deliveryService = deliveryService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Delivery> createDelivery(
            @RequestBody Delivery delivery) {

        return ResponseEntity.ok(
                deliveryService.createDelivery(delivery)
        );
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Delivery>>
    getAllDeliveries() {

        return ResponseEntity.ok(
                deliveryService.getAllDeliveries()
        );
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Delivery>
    getDeliveryById(@PathVariable Long id) {

        return deliveryService
                .getDeliveryById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Delivery>
    updateDelivery(
            @PathVariable Long id,
            @RequestBody Delivery delivery) {

        return ResponseEntity.ok(
                deliveryService.updateDelivery(
                        id,
                        delivery
                )
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteDelivery(@PathVariable Long id) {

        deliveryService.deleteDelivery(id);

        return ResponseEntity.noContent().build();
    }

    // UPDATE STATUS
    @PatchMapping("/{id}/status")
    public ResponseEntity<Delivery>
    updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                deliveryService.updateStatus(
                        id,
                        status
                )
        );
    }

    // NEW: assign / reassign a delivery to a delivery person
    @PatchMapping("/{id}/assign")
    public ResponseEntity<Delivery>
    assignDeliveryPerson(
            @PathVariable Long id,
            @RequestParam String deliveryPerson) {

        return ResponseEntity.ok(
                deliveryService.assignDeliveryPerson(
                        id,
                        deliveryPerson
                )
        );
    }

    // NEW: capture proof of delivery and mark as DELIVERED
    @PatchMapping("/{id}/proof")
    public ResponseEntity<Delivery>
    captureProofOfDelivery(
            @PathVariable Long id,
            @RequestParam String proofOfDelivery) {

        return ResponseEntity.ok(
                deliveryService.captureProofOfDelivery(
                        id,
                        proofOfDelivery
                )
        );
    }

    // NEW: deliveries assigned to a specific delivery person
    @GetMapping("/person/{deliveryPerson}")
    public ResponseEntity<List<Delivery>>
    getDeliveriesByPerson(@PathVariable String deliveryPerson) {

        return ResponseEntity.ok(
                deliveryService.getDeliveriesByPerson(deliveryPerson)
        );
    }

    // NEW: filter deliveries by status, e.g. /api/deliveries/status/PENDING
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Delivery>>
    getDeliveriesByStatus(@PathVariable String status) {

        return ResponseEntity.ok(
                deliveryService.getDeliveriesByStatus(status)
        );
    }

    // NEW: deliveries tied to a given order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Delivery>>
    getDeliveriesByOrderId(@PathVariable Long orderId) {

        return ResponseEntity.ok(
                deliveryService.getDeliveriesByOrderId(orderId)
        );
    }

    // NEW: search deliveries by customer name, e.g. /api/deliveries/search?customerName=john
    @GetMapping("/search")
    public ResponseEntity<List<Delivery>>
    searchByCustomerName(@RequestParam String customerName) {

        return ResponseEntity.ok(
                deliveryService.searchByCustomerName(customerName)
        );
    }
}
