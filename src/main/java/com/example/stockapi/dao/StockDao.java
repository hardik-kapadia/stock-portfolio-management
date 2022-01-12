package com.example.stockapi.dao;

import com.example.stockapi.model.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDao extends JpaRepository<Stock, String> {
}
