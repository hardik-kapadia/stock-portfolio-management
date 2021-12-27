package com.example.stockapi;

import com.example.stockapi.dao.StockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockApiApplication.class, args);
    }

}
