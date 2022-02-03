package com.example.stockapi.dao;

import com.example.stockapi.model.role.ERole;
import com.example.stockapi.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleDao extends JpaRepository<Role,Long> {

    Optional<Role> findByName(ERole name);

}
