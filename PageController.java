package Pharmacy.Management.System.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/medicines")
    public String medicinesPage() {
        return "medicines";
    }

    @GetMapping("/inventory")
    public String inventoryPage() {
        return "inventory";
    }

    @GetMapping("/prescriptions")
    public String prescriptionsPage() {
        return "prescription";
    }

    @GetMapping("/prescription-items")
    public String prescriptionItemsPage() {
        return "prescription-items";
    }
}