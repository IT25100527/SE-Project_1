package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    List<LoginLog> findByUser_IdOrderByLoginTimeDesc(Long userId);

    void deleteByUser_Id(Long userId);
}