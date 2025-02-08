package com.ecommerce.javaecom.repositories;

import com.ecommerce.javaecom.model.AppRole;
import com.ecommerce.javaecom.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
