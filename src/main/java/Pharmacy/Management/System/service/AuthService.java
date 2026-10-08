package Pharmacy.Management.System.service;

import Pharmacy.Management.System.dto.CustomerRegisterRequest;
import Pharmacy.Management.System.dto.LoginRequest;
import Pharmacy.Management.System.entity.*;
import Pharmacy.Management.System.exception.ApiException;
import Pharmacy.Management.System.exception.AuthException;
import Pharmacy.Management.System.repository.LoginLogRepository;
import Pharmacy.Management.System.repository.RoleRepository;
import Pharmacy.Management.System.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final String SESSION_USER_ID = "USER_ID";
    public static final String SESSION_ROLE = "USER_ROLE";
    public static final String SESSION_NAME = "USER_NAME";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final LoginLogRepository loginLogRepository;


    // =========================================================
    // CUSTOMER REGISTRATION
    // =========================================================

    @Transactional
    public User registerCustomer(CustomerRegisterRequest request) {

        // Check duplicate email
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ApiException(
                    "Email is already registered"
            );
        }

        // Check duplicate phone
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ApiException(
                    "Phone number is already registered"
            );
        }

        // Get CUSTOMER role
        Role customerRole = roleRepository
                .findByName(RoleName.CUSTOMER)
                .orElseThrow(() ->
                        new ApiException(
                                "Customer role is not configured"
                        )
                );

        // Create customer
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(customerRole)

                // Email is NOT verified initially
                .emailVerified(false)

                // Phone verification can be handled separately
                .phoneVerified(false)

                .status(UserStatus.ACTIVE)
                .build();


        // =====================================================
        // GENERATE 6-DIGIT OTP
        // =====================================================

        String verificationCode =
                generateVerificationCode();

        user.setVerificationCode(
                verificationCode
        );


        // =====================================================
        // OTP EXPIRY - 10 MINUTES
        // =====================================================

        user.setVerificationCodeExpiry(
                LocalDateTime.now().plusMinutes(10)
        );


        // =====================================================
        // SAVE USER + OTP
        // =====================================================

        userRepository.save(user);


        // =====================================================
        // SEND OTP TO CUSTOMER EMAIL
        // =====================================================

        emailService.sendVerificationCode(
                user.getEmail(),
                user.getFullName(),
                verificationCode
        );

        return user;
    }


    // =========================================================
    // GENERATE 6-DIGIT VERIFICATION CODE
    // =========================================================

    private String generateVerificationCode() {

        SecureRandom random = new SecureRandom();

        return String.format(
                "%06d",
                random.nextInt(1_000_000)
        );
    }


    // =========================================================
    // VERIFY EMAIL USING OTP
    // =========================================================

    @Transactional
    public void verifyEmailCode(
            String email,
            String code
    ) {

        // Find customer
        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ApiException(
                                "Customer not found."
                        )
                );


        // =====================================================
        // CHECK ALREADY VERIFIED
        // =====================================================

        if (user.isEmailVerified()) {
            throw new ApiException(
                    "Email is already verified."
            );
        }


        // =====================================================
        // CHECK OTP EXISTS
        // =====================================================

        if (user.getVerificationCode() == null) {
            throw new ApiException(
                    "No verification code found. Please request a new code."
            );
        }


        // =====================================================
        // CHECK OTP
        // =====================================================

        if (!user.getVerificationCode().equals(code)) {
            throw new ApiException(
                    "Invalid verification code."
            );
        }


        // =====================================================
        // CHECK OTP EXPIRY
        // =====================================================

        if (user.getVerificationCodeExpiry() == null ||
                user.getVerificationCodeExpiry()
                        .isBefore(LocalDateTime.now())) {

            throw new ApiException(
                    "Verification code has expired. Please request a new code."
            );
        }


        // =====================================================
        // EMAIL VERIFIED
        // =====================================================

        user.setEmailVerified(true);


        // =====================================================
        // REMOVE OTP AFTER SUCCESSFUL VERIFICATION
        // =====================================================

        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);


        // Save changes
        userRepository.save(user);
    }


    // =========================================================
    // RESEND VERIFICATION OTP
    // =========================================================

    @Transactional
    public void resendVerification(String email) {

        // Find customer
        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ApiException(
                                "No account found for that email"
                        )
                );


        // Already verified
        if (user.isEmailVerified()) {
            throw new ApiException(
                    "This email is already verified"
            );
        }


        // =====================================================
        // GENERATE NEW OTP
        // =====================================================

        String newCode =
                generateVerificationCode();


        // =====================================================
        // SAVE NEW OTP
        // =====================================================

        user.setVerificationCode(
                newCode
        );


        // New OTP expires after 10 minutes
        user.setVerificationCodeExpiry(
                LocalDateTime.now().plusMinutes(10)
        );


        userRepository.save(user);


        // =====================================================
        // SEND NEW OTP
        // =====================================================

        emailService.sendVerificationCode(
                user.getEmail(),
                user.getFullName(),
                newCode
        );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @Transactional
    public User authenticate(
            LoginRequest request,
            RoleScope scope,
            HttpServletRequest httpRequest
    ) {

        User user = userRepository
                .findByEmailIgnoreCase(
                        request.getEmail()
                )
                .orElse(null);


        // =====================================================
        // BASIC LOGIN CHECK
        // =====================================================

        boolean success = user != null
                && passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )
                && scopeMatches(
                user.getRole().getName(),
                scope
        )
                && user.getStatus() == UserStatus.ACTIVE;


        // Save login attempt
        logAttempt(
                user,
                request.getEmail(),
                success,
                httpRequest
        );


        // =====================================================
        // INVALID USER / WRONG ROLE
        // =====================================================

        if (user == null ||
                !scopeMatches(
                        user.getRole().getName(),
                        scope
                )) {

            throw new AuthException(
                    "Invalid email or password for this login page"
            );
        }


        // =====================================================
        // SUSPENDED ACCOUNT
        // =====================================================

        if (user.getStatus() == UserStatus.SUSPENDED) {

            throw new AuthException(
                    "Your account has been suspended. Contact the administrator."
            );
        }


        // =====================================================
        // INACTIVE ACCOUNT
        // =====================================================

        if (user.getStatus() == UserStatus.INACTIVE) {

            throw new AuthException(
                    "Your account is inactive. Contact the administrator."
            );
        }


        // =====================================================
        // PASSWORD CHECK
        // =====================================================

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {

            throw new AuthException(
                    "Invalid email or password"
            );
        }


        // =====================================================
        // CUSTOMER EMAIL VERIFICATION CHECK
        // =====================================================

        if (scope == RoleScope.CUSTOMER &&
                !user.isEmailVerified()) {

            throw new AuthException(
                    "Please verify your email before logging in"
            );
        }


        return user;
    }


    // =========================================================
    // ROLE CHECK
    // =========================================================

    private boolean scopeMatches(
            RoleName role,
            RoleScope scope
    ) {

        if (scope == RoleScope.CUSTOMER) {
            return role == RoleName.CUSTOMER;
        }

        if (scope == RoleScope.ADMIN) {
            return role == RoleName.ADMIN
                    || role == RoleName.PHARMACY_OWNER;
        }

        if (scope == RoleScope.STAFF) {
            return role == RoleName.INVENTORY_MANAGER
                    || role == RoleName.DELIVERY_STAFF
                    || role == RoleName.PHARMACIST
                    || role == RoleName.CASHIER
                    || role == RoleName.PURCHASE_MANAGER;
        }

        return false;
    }

    // =========================================================
    // LOGIN ATTEMPT LOGGING
    // =========================================================

    private void logAttempt(
            User user,
            String attemptedEmail,
            boolean success,
            HttpServletRequest req
    ) {

        LoginLog log = LoginLog.builder()
                .user(user)
                .attemptedEmail(attemptedEmail)
                .ipAddress(req.getRemoteAddr())
                .userAgent(req.getHeader("User-Agent"))
                .success(success)
                .build();

        loginLogRepository.save(log);
    }


    // =========================================================
    // CREATE SESSION
    // =========================================================

    public void createSession(
            HttpSession session,
            User user
    ) {

        session.setAttribute(
                SESSION_USER_ID,
                user.getId()
        );

        session.setAttribute(
                SESSION_ROLE,
                user.getRole().getName().name()
        );

        session.setAttribute(
                SESSION_NAME,
                user.getFullName()
        );
    }


    // =========================================================
    // ROLE SCOPE
    // =========================================================

    public enum RoleScope {
        CUSTOMER,
        STAFF,
        ADMIN
    }
}