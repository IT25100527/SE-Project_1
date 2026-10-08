package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.dto.ApiResponse;
import Pharmacy.Management.System.dto.RoleDetailResponse;
import Pharmacy.Management.System.dto.RoleResponse;
import Pharmacy.Management.System.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;


    // =========================
    // GET ALL ROLES
    // =========================

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {

        return ApiResponse.ok(
                "Roles loaded successfully",
                roleService.getAllRoles()
        );
    }


    // =========================
    // GET ROLE + PERMISSIONS
    // =========================

    @GetMapping("/{id}")
    public ApiResponse<RoleDetailResponse> getRole(
            @PathVariable Long id) {

        return ApiResponse.ok(
                "Role loaded successfully",
                roleService.getRole(id)
        );
    }
}