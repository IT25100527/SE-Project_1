package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.dto.ApiResponse;
import Pharmacy.Management.System.dto.LoginRequest;
import Pharmacy.Management.System.dto.UserResponse;
import Pharmacy.Management.System.entity.User;
import Pharmacy.Management.System.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffAuthController {

    private final AuthService authService;

    // =========================================================
    // STAFF LOGIN
    // =========================================================

    @PostMapping("/login")
    public ApiResponse<UserResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpSession session) {

        User user = authService.authenticate(
                request,
                AuthService.RoleScope.STAFF,
                httpRequest
        );

        authService.createSession(session, user);

        return ApiResponse.ok(
                "Login successful",
                UserResponse.from(user)
        );
    }


    // =========================================================
    // STAFF LOGOUT
    // =========================================================

    @PostMapping("/logout")
    public ApiResponse<String> logout(
            HttpSession session) {

        session.invalidate();

        return ApiResponse.ok(
                "Logged out"
        );
    }
}