package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.dto.ApiResponse;
import Pharmacy.Management.System.dto.ChangePasswordRequest;
import Pharmacy.Management.System.dto.SelfProfileUpdateRequest;
import Pharmacy.Management.System.dto.UserResponse;
import Pharmacy.Management.System.service.AuthService;
import Pharmacy.Management.System.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * "My Account" - what a logged-in user (customer or staff) can view/change about
 * themselves. Always operates on the session's own user id, never a path id, so it
 * needs no role check beyond "is logged in" (enforced by AuthInterceptor on /api/me/**).
 */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<UserResponse> getMyProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute(AuthService.SESSION_USER_ID);
        return ApiResponse.ok("Profile", userService.getById(userId));
    }

    @PutMapping
    public ApiResponse<UserResponse> updateMyProfile(@Valid @RequestBody SelfProfileUpdateRequest request,
                                                     HttpSession session) {
        Long userId = (Long) session.getAttribute(AuthService.SESSION_USER_ID);
        UserResponse updated = userService.updateOwnProfile(userId, request);
        session.setAttribute(AuthService.SESSION_NAME, updated.getFullName());
        return ApiResponse.ok("Profile updated", updated);
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changeMyPassword(@Valid @RequestBody ChangePasswordRequest request,
                                                HttpSession session) {
        Long userId = (Long) session.getAttribute(AuthService.SESSION_USER_ID);
        userService.changeOwnPassword(userId, request);
        return ApiResponse.ok("Password changed successfully");
    }
}