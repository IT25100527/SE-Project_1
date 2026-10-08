package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRole_Id(Long roleId);
}