package Pharmacy.Management.System.delivery.exception;

public class DeliveryNotFoundException extends RuntimeException {

    public DeliveryNotFoundException(Long id) {
        super("Delivery not found with id: " + id);
    }
}
