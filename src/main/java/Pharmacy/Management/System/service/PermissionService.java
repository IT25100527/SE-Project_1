package Pharmacy.Management.System.service;

import Pharmacy.Management.System.entity.RolePermission;
import Pharmacy.Management.System.entity.User;
import Pharmacy.Management.System.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public boolean hasPermission(
            User user,
            String permissionName
    ) {

        if (user == null || user.getRole() == null) {
            return false;
        }

        Long roleId = user.getRole().getId();

        List<RolePermission> rolePermissions =
                rolePermissionRepository.findByRole_Id(roleId);

        return rolePermissions.stream()
                .anyMatch(rp ->
                        rp.getPermission()
                                .getPermissionName()
                                .equalsIgnoreCase(permissionName)
                );
    }
}