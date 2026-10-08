package Pharmacy.Management.System.controller;

import Pharmacy.Management.System.dto.ApiResponse;
import Pharmacy.Management.System.dto.CustomerRegisterRequest;
import Pharmacy.Management.System.dto.LoginRequest;
import Pharmacy.Management.System.dto.UserResponse;
import Pharmacy.Management.System.entity.User;
import Pharmacy.Management.System.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

/**
 * Customer authentication endpoints.
 */
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerAuthController {

    private final AuthService authService;


    // ==========================================
    // CUSTOMER REGISTRATION
    // ==========================================

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(
            @Valid @RequestBody CustomerRegisterRequest request) {

        User user = authService.registerCustomer(request);

        return ApiResponse.ok(
                "Registration successful. Please check your email to verify your account.",
                UserResponse.from(user)
        );
    }


    // ==========================================
    // EMAIL VERIFICATION
    // ==========================================

    @PostMapping("/verify-email")
    public ApiResponse<String> verifyEmail(
            @RequestParam String email,
            @RequestParam String code) {

        authService.verifyEmailCode(email, code);

        return ApiResponse.ok(
                "Email verified successfully. You can now log in."
        );
    }


    // ==========================================
    // RESEND VERIFICATION CODE
    // ==========================================

    @PostMapping("/resend-verification")
    public ApiResponse<String> resend(
            @RequestParam String email) {

        authService.resendVerification(email);

        return ApiResponse.ok(
                "Verification code resent."
        );
    }


    // ==========================================
    // CUSTOMER LOGIN
    // ==========================================

    @PostMapping("/login")
    public ApiResponse<UserResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpSession session) {

        User user = authService.authenticate(
                request,
                AuthService.RoleScope.CUSTOMER,
                httpRequest
        );

        authService.createSession(session, user);

        return ApiResponse.ok(
                "Login successful",
                UserResponse.from(user)
        );
    }


    // ==========================================
    // LOGOUT
    // ==========================================

    @PostMapping("/logout")
    public ApiResponse<String> logout(
            HttpSession session) {

        session.invalidate();

        return ApiResponse.ok(
                "Logged out"
        );
    }
}