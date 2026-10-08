package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.dto.*;
import Pharmacy.Management.System.entity.User;
import Pharmacy.Management.System.entity.UserStatus;
import Pharmacy.Management.System.exception.ApiException;
import Pharmacy.Management.System.repository.UserRepository;
import Pharmacy.Management.System.service.AuthService;
import Pharmacy.Management.System.service.PermissionService;
import Pharmacy.Management.System.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin / Pharmacy Owner user management.
 *
 * Access is protected by:
 * 1. AuthInterceptor - Admin / Pharmacy Owner access
 * 2. PermissionService - USER_VIEW / USER_UPDATE / USER_DELETE
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final PermissionService permissionService;
    private final UserRepository userRepository;


    // =========================
    // CREATE STAFF
    // =========================

    @PostMapping("/staff")
    public ApiResponse<UserResponse> createStaff(
            @Valid @RequestBody StaffCreateRequest request) {

        return ApiResponse.ok(
                "Staff account created",
                userService.createStaffUser(request)
        );
    }


    // =========================
    // LIST STAFF
    // =========================

    @GetMapping("/staff")
    public ApiResponse<List<UserResponse>> listStaff() {

        return ApiResponse.ok(
                "Staff list",
                userService.listStaff()
        );
    }


    // =========================
    // LIST CUSTOMERS
    // =========================

    @GetMapping("/customers")
    public ApiResponse<List<UserResponse>> listCustomers() {

        return ApiResponse.ok(
                "Customer list",
                userService.listCustomers()
        );
    }


    // =========================
    // GET USER
    // =========================

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(
            @PathVariable Long id) {

        return ApiResponse.ok(
                "User detail",
                userService.getById(id)
        );
    }


    // =========================
    // UPDATE USER
    // =========================

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            HttpSession session) {

        User currentUser = getCurrentUser(session);

        if (!permissionService.hasPermission(
                currentUser,
                "USER_UPDATE")) {

            throw new ApiException(
                    "You do not have permission to update users"
            );
        }

        return ApiResponse.ok(
                "User updated",
                userService.updateUser(id, request)
        );
    }


    // =========================
    // CHANGE USER STATUS
    // =========================

    @PatchMapping("/{id}/status")
    public ApiResponse<UserResponse> setStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status) {

        return ApiResponse.ok(
                "Status updated",
                userService.setStatus(id, status)
        );
    }


    // =========================
    // DELETE USER
    // =========================

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(
            @PathVariable Long id,
            HttpSession session) {

        User currentUser = getCurrentUser(session);

        if (!permissionService.hasPermission(
                currentUser,
                "USER_DELETE")) {

            throw new ApiException(
                    "You do not have permission to delete users"
            );
        }

        userService.deleteUser(id);

        return ApiResponse.ok("User deleted");
    }


    // =========================
    // GET CURRENT LOGGED-IN USER
    // =========================

    private User getCurrentUser(HttpSession session) {

        Long userId =
                (Long) session.getAttribute(
                        AuthService.SESSION_USER_ID
                );

        if (userId == null) {
            throw new ApiException("Not logged in");
        }

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ApiException("User not found"));
    }
}