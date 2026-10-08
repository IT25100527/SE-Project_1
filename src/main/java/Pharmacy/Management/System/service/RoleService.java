package Pharmacy.Management.System.service;

import Pharmacy.Management.System.dto.RoleDetailResponse;
import Pharmacy.Management.System.dto.RoleResponse;
import Pharmacy.Management.System.entity.Role;
import Pharmacy.Management.System.exception.ApiException;
import Pharmacy.Management.System.repository.RolePermissionRepository;
import Pharmacy.Management.System.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;


    public List<RoleResponse> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(RoleResponse::from)
                .toList();
    }


    public RoleDetailResponse getRole(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException("Role not found"));

        List<String> permissions =
                rolePermissionRepository.findByRole_Id(id)
                        .stream()
                        .map(rp ->
                                rp.getPermission().getPermissionName()
                        )
                        .toList();

        return RoleDetailResponse.builder()
                .id(role.getId())
                .name(role.getName().name())
                .description(role.getDescription())
                .permissions(permissions)
                .build();
    }
}