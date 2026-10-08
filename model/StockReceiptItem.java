package Pharmacy.Management.System.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_receipt_item")
public class StockReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer receiptItemId;

    @ManyToOne
    @JoinColumn(name = "receipt_id", nullable = false)
    private StockReceipt stockReceipt;

    @ManyToOne
    @JoinColumn(name = "po_item_id", nullable = false)
    private PurchaseOrderItem purchaseOrderItem;

    private Integer receivedQty;

    public StockReceiptItem() {}

    public Integer getReceiptItemId() { return receiptItemId; }
    public void setReceiptItemId(Integer receiptItemId) { this.receiptItemId = receiptItemId; }
    public StockReceipt getStockReceipt() { return stockReceipt; }
    public void setStockReceipt(StockReceipt stockReceipt) { this.stockReceipt = stockReceipt; }
    public PurchaseOrderItem getPurchaseOrderItem() { return purchaseOrderItem; }
    public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) { this.purchaseOrderItem = purchaseOrderItem; }
    public Integer getReceivedQty() { return receivedQty; }
    public void setReceivedQty(Integer receivedQty) { this.receivedQty = receivedQty; }
}