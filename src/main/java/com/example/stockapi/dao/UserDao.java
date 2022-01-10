package com.example.stockapi.dao;

import com.example.stockapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User,String> {



}
