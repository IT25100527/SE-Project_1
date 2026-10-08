package Pharmacy.Management.System.repository;

import Pharmacy.Management.System.entity.RoleName;
import Pharmacy.Management.System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email - case insensitive
    Optional<User> findByEmailIgnoreCase(String email);

    // Find user by phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Check whether email already exists
    boolean existsByEmailIgnoreCase(String email);

    // Check whether phone number already exists
    boolean existsByPhoneNumber(String phoneNumber);

    // Find users by role
    List<User> findByRole_Name(RoleName roleName);
}