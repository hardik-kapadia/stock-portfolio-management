package com.example.stockapi.controller;

import com.example.stockapi.dao.StockDataService;
import com.example.stockapi.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    StockDataService stockDataService;

    @Autowired
    public UserController(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {

        return List.of(new User("Hardik", "abc@xyz.com", "8879563067", "AQ2QH5"), new User("Mitesh", "a@bc.com", "9833363067", "AQ2QH5"));

    }

}
