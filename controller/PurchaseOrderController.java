package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.dto.PurchaseOrderForm;
import Pharmacy.Management.System.model.PurchaseOrder;
import Pharmacy.Management.System.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    public String listPurchaseOrders(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.getAllPurchaseOrders());
        return "purchase-order-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("suppliers", purchaseOrderService.getAllSuppliers());
        model.addAttribute("medicines", purchaseOrderService.getAllMedicines());
        model.addAttribute("users", purchaseOrderService.getAllUsers());
        return "purchase-order-form";
    }

    @PostMapping
    public String createPurchaseOrder(PurchaseOrderForm form) {
        purchaseOrderService.createPurchaseOrder(form);
        return "redirect:/purchase-orders";
    }

    @GetMapping("/{id}")
    public String viewPurchaseOrder(@PathVariable Integer id, Model model) {
        PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(id);
        model.addAttribute("po", po);
        model.addAttribute("users", purchaseOrderService.getAllUsers());
        return "purchase-order-view";
    }

    @PostMapping("/{id}/receive")
    public String receiveStock(@PathVariable Integer id,
                               @RequestParam Long userId,
                               @RequestParam List<Integer> poItemIds,
                               @RequestParam List<Integer> receivedQtys) {
        purchaseOrderService.receiveStock(id, userId, poItemIds, receivedQtys);
        return "redirect:/purchase-orders/" + id;
    }
}