package Pharmacy.Management.System.config;

import Pharmacy.Management.System.entity.Role;
import Pharmacy.Management.System.entity.RoleName;
import Pharmacy.Management.System.entity.User;
import Pharmacy.Management.System.entity.UserStatus;
import Pharmacy.Management.System.repository.RoleRepository;
import Pharmacy.Management.System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Seeds the fixed set of roles and a default administrator account on startup,
 * so the system is usable immediately after a fresh deploy.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.email}")
    private String defaultAdminEmail;

    @Value("${app.default-admin.password}")
    private String defaultAdminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedDefaultAdmin();
    }

    private void seedRoles() {
        Map<RoleName, String> descriptions = new LinkedHashMap<>();
        descriptions.put(RoleName.ADMIN, "Manages users, system settings, permissions, and oversees all modules.");
        descriptions.put(RoleName.PHARMACY_OWNER, "Owns the pharmacy business; full visibility across modules and financials, and manages Administrators.");
        descriptions.put(RoleName.INVENTORY_MANAGER, "Monitors stock levels, updates inventory, tracks expiry dates, and manages medicine availability.");
        descriptions.put(RoleName.DELIVERY_STAFF, "Delivers medicines to customers, updates delivery status, and confirms completed deliveries.");
        descriptions.put(RoleName.PHARMACIST, "Dispenses medicines, manages prescriptions, handles sales and billing, and checks inventory.");
        descriptions.put(RoleName.CASHIER, "Processes sales, generates bills, accepts payments, and prints receipts.");
        descriptions.put(RoleName.PURCHASE_MANAGER, "Creates purchase orders, manages purchases, and coordinates with suppliers.");
        descriptions.put(RoleName.CUSTOMER, "Browses medicines, places orders, and tracks their own deliveries and prescriptions.");

        descriptions.forEach((name, desc) ->
                roleRepository.findByName(name).orElseGet(() ->
                        roleRepository.save(Role.builder().name(name).description(desc).build())));
    }

    private void seedDefaultAdmin() {
        if (userRepository.existsByEmailIgnoreCase(defaultAdminEmail)) {
            return;
        }
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role missing after seeding"));

        User admin = User.builder()
                .fullName("System Administrator")
                .email(defaultAdminEmail.toLowerCase())
                .phoneNumber("0770000000")
                .passwordHash(passwordEncoder.encode(defaultAdminPassword))
                .role(adminRole)
                .emailVerified(true)
                .phoneVerified(true)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(admin);
    }
}