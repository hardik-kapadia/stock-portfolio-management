package com.example.stockapi.dao;

import com.example.stockapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDao extends JpaRepository<User,String> {

    Optional<User> getUserByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByMobileNumber(String mobileNumber);

    Boolean existsByAccountNumber(String accountNumber);

}
