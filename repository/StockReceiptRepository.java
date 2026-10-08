package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.model.StockReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReceiptRepository extends JpaRepository<StockReceipt, Integer> {
}