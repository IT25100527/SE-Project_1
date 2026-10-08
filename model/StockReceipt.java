package Pharmacy.Management.System.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import Pharmacy.Management.System.entity.User;

@Entity
@Table(name = "stock_receipt")
public class StockReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer receiptId;

    @ManyToOne
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate receivedDate;

    @OneToMany(mappedBy = "stockReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockReceiptItem> items;

    public StockReceipt() {}

    public Integer getReceiptId() { return receiptId; }
    public void setReceiptId(Integer receiptId) { this.receiptId = receiptId; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }
    public List<StockReceiptItem> getItems() { return items; }
    public void setItems(List<StockReceiptItem> items) { this.items = items; }
}