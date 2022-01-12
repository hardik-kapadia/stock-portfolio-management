package com.example.stockapi.dao;

import com.example.stockapi.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentDao extends JpaRepository<Investment,Integer> {
}
