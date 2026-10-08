package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.model.StockReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReceiptItemRepository extends JpaRepository<StockReceiptItem, Integer> {
}