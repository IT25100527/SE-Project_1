package Pharmacy.Management.System.service;

import Pharmacy.Management.System.dto.PurchaseOrderForm;
import Pharmacy.Management.System.model.*;
import Pharmacy.Management.System.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Pharmacy.Management.System.entity.Supplier;
import Pharmacy.Management.System.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseOrderService {

    @Autowired private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired private PurchaseOrderItemRepository purchaseOrderItemRepository;
    @Autowired private StockReceiptRepository stockReceiptRepository;
    @Autowired private StockReceiptItemRepository stockReceiptItemRepository;
    @Autowired private MedicineRepository medicineRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private UserRepository userRepository;

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder getPurchaseOrderById(Integer id) {
        return purchaseOrderRepository.findById(id).orElse(null);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // UC-05 step 3-5: create a purchase order with its line items
    public PurchaseOrder createPurchaseOrder(PurchaseOrderForm form) {
        Supplier supplier = supplierRepository.findById(form.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
                 User user = userRepository.findById(form.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplier(supplier);
        po.setUser(user);
        po.setOrderDate(LocalDate.now());
        po.setStatus("PENDING");

        List<PurchaseOrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < form.getMedicineIds().size(); i++) {
            Long medicineId = form.getMedicineIds().get(i);
            Integer qty = form.getQuantities().get(i);
            BigDecimal cost = form.getUnitCosts().get(i);

            // Skip blank rows (user didn't fill this line)
            if (medicineId == null || qty == null || cost == null) continue;

            Medicine medicine = medicineRepository.findById(medicineId)
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(po);
            item.setMedicine(medicine);
            item.setOrderedQty(qty);
            item.setUnitCost(cost);
            items.add(item);

            total = total.add(cost.multiply(BigDecimal.valueOf(qty)));
        }

        po.setItems(items);
        po.setTotalCost(total);

        return purchaseOrderRepository.save(po); // cascades and saves items too
    }

    // UC-05 step 6-7: confirm received quantities (full or partial), update stock
    public void receiveStock(Integer poId, Long userId, List<Integer> poItemIds, List<Integer> receivedQtys) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StockReceipt receipt = new StockReceipt();
        receipt.setPurchaseOrder(po);
        receipt.setUser(user);
        receipt.setReceivedDate(LocalDate.now());

        List<StockReceiptItem> receiptItems = new ArrayList<>();
        boolean fullyReceived = true;

        for (int i = 0; i < poItemIds.size(); i++) {
            Integer poItemId = poItemIds.get(i);
            Integer receivedQty = receivedQtys.get(i);
            if (receivedQty == null || receivedQty <= 0) continue;

            PurchaseOrderItem poItem = purchaseOrderItemRepository.findById(poItemId)
                    .orElseThrow(() -> new RuntimeException("Purchase order item not found"));

            StockReceiptItem receiptItem = new StockReceiptItem();
            receiptItem.setStockReceipt(receipt);
            receiptItem.setPurchaseOrderItem(poItem);
            receiptItem.setReceivedQty(receivedQty);
            receiptItems.add(receiptItem);

            // Alternative flow 6a: partial receipt check
            if (receivedQty < poItem.getOrderedQty()) {
                fullyReceived = false;
            }

            // Update medicine stock level (postcondition: inventory replenished)
            Medicine medicine = poItem.getMedicine();
            //medicine.setCurrentStock(medicine.getCurrentStock() + receivedQty);
            //medicineRepository.save(medicine);
        }

        receipt.setItems(receiptItems);
        stockReceiptRepository.save(receipt);

        po.setStatus(fullyReceived ? "COMPLETED" : "PARTIALLY_RECEIVED");
        purchaseOrderRepository.save(po);
    }
}