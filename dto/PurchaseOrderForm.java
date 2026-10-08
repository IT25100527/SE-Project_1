package Pharmacy.Management.System.dto;

import java.math.BigDecimal;
import java.util.List;

public class PurchaseOrderForm {

    private Long supplierId;
    private Long userId;
    private List<Long> medicineIds;
    private List<Integer> quantities;
    private List<BigDecimal> unitCosts;

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<Long> getMedicineIds() { return medicineIds; }
    public void setMedicineIds(List<Long> medicineIds) { this.medicineIds = medicineIds; }
    public List<Integer> getQuantities() { return quantities; }
    public void setQuantities(List<Integer> quantities) { this.quantities = quantities; }
    public List<BigDecimal> getUnitCosts() { return unitCosts; }
    public void setUnitCosts(List<BigDecimal> unitCosts) { this.unitCosts = unitCosts; }
}