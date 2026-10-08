package Pharmacy.Management.System.service;

import Pharmacy.Management.System.dto.ChangePasswordRequest;
import Pharmacy.Management.System.dto.SelfProfileUpdateRequest;
import Pharmacy.Management.System.dto.StaffCreateRequest;
import Pharmacy.Management.System.dto.UserResponse;
import Pharmacy.Management.System.dto.UserUpdateRequest;
import Pharmacy.Management.System.entity.Role;
import Pharmacy.Management.System.entity.RoleName;
import Pharmacy.Management.System.entity.User;
import Pharmacy.Management.System.entity.UserStatus;
import Pharmacy.Management.System.exception.ApiException;
import Pharmacy.Management.System.repository.EmailVerificationTokenRepository;
import Pharmacy.Management.System.repository.LoginLogRepository;
import Pharmacy.Management.System.repository.RoleRepository;
import Pharmacy.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final LoginLogRepository loginLogRepository;
    private final PermissionService permissionService;

    @Transactional
    public UserResponse createStaffUser(StaffCreateRequest request) {
        if (request.getRole() == RoleName.CUSTOMER) {
            throw new ApiException("Use the customer registration flow for customer accounts");
        }
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ApiException("Email is already registered");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ApiException("Phone number is already registered");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ApiException("Role not found: " + request.getRole()));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .emailVerified(true)   // staff accounts are created and trusted by admin
                .phoneVerified(true)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        emailService.sendStaffWelcomeEmail(user.getEmail(), user.getFullName(), request.getPassword(), role.getName().name());
        return UserResponse.from(user);
    }

    public List<UserResponse> listStaff() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().getName() != RoleName.CUSTOMER)
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public List<UserResponse> listCustomers() {
        return userRepository.findByRole_Name(RoleName.CUSTOMER).stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public UserResponse getById(Long id) {
        return UserResponse.from(findById(id));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findById(id);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
                throw new ApiException("Email is already registered");
            }
            user.setEmail(request.getEmail().toLowerCase());
            user.setEmailVerified(false);
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()
                && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new ApiException("Phone number is already registered");
            }
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPhoneVerified(false);
        }
        if (request.getRole() != null) {
            Role role = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new ApiException("Role not found: " + request.getRole()));
            user.setRole(role);
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse setStatus(Long id, UserStatus status) {
        User user = findById(id);
        user.setStatus(status);
        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        loginLogRepository.deleteByUser_Id(id);
        userRepository.delete(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    // ---------------- Self-service ("My Account") ----------------
    // Deliberately narrower than updateUser(): a user may only change their own
    // name/phone and password here, never their own role or status.

    @Transactional
    public UserResponse updateOwnProfile(Long id, SelfProfileUpdateRequest request) {
        User user = findById(id);

        user.setFullName(request.getFullName());

        if (!request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new ApiException("Phone number is already registered");
            }
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPhoneVerified(false);
        }

        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public void changeOwnPassword(Long id, ChangePasswordRequest request) {
        User user = findById(id);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new ApiException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}