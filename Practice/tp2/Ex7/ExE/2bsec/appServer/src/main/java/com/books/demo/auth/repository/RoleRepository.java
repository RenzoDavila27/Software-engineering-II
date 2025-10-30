package com.books.demo.auth.repository;

import com.books.demo.auth.entity.Role;
import com.books.demo.auth.enums.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}

