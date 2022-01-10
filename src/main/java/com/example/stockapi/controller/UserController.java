package com.example.stockapi.controller;

import com.example.stockapi.dao.UserDao;
import com.example.stockapi.exception.ApiException;
import com.example.stockapi.model.Investment;
import com.example.stockapi.model.stock.Stock;
import com.example.stockapi.repository.StockDataRepo;
import com.example.stockapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    StockDataRepo stockDataRepo;
    UserDao userDao;

    @Autowired
    public UserController(StockDataRepo stockDataRepo, UserDao userDao) {
        this.stockDataRepo = stockDataRepo;
        this.userDao = userDao;
    }

    private User getUserFromNumber(String userAccountNumber) throws IllegalArgumentException {
        Optional<User> optionalUser = this.userDao.findById(userAccountNumber);

        if (optionalUser.isEmpty())
            throw new IllegalArgumentException("User doesn't exists");

        return optionalUser.get();
    }

    private void updateUserInvestments(User user) {
        user.getInvestments().forEach(i -> {
            try {
                this.stockDataRepo.updateStock(i.getStock());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        });
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return this.userDao.findAll();
    }

    @GetMapping("/{userAccountNumber}/investments/")
    public List<Investment> getUserInvestments(@PathVariable String userAccountNumber) {

        User user = this.getUserFromNumber(userAccountNumber);

        this.updateUserInvestments(user);

        return user.getInvestments();
    }

    @GetMapping("/{userAccountNumber}/info")
    public User getUserInfo(@PathVariable String userAccountNumber) {

        User user = this.getUserFromNumber(userAccountNumber);

        this.updateUserInvestments(user);

        return user;

    }

    private Investment getInvestmentFromId(User user, Integer investmentId) {
        Investment investment = null;

        List<Investment> userInvestments = user.getInvestments();

        for (Investment userInvestment : userInvestments) {
            if (userInvestment.getId().intValue() == investmentId.intValue()) {
                investment = userInvestment;
                break;
            }
        }

        return investment;
    }


    @PostMapping("/{userAccountNumber}/sell/")
    public void sellStock(@PathVariable String userAccountNumber, @RequestBody Map<String, String> payload) {

        String tempInvestmentId = payload.getOrDefault("investmentId", null);

        if (tempInvestmentId == null)
            throw new IllegalArgumentException("No investment id provided");

        int investmentId = Integer.parseInt(tempInvestmentId);

        User user = this.getUserFromNumber(userAccountNumber);

        this.updateUserInvestments(user);

        Investment investment = this.getInvestmentFromId(user, investmentId);

        if (investment == null)
            throw new IllegalArgumentException("Invalid investment id");

        int quantity = Integer.parseInt(payload.getOrDefault("quantity", investment.getQuantity().toString()));

        Double sellingPrice = Double.parseDouble(payload.getOrDefault("sellingPrice", investment.getStock().getLTP().toString()));

        investment.sell(quantity, sellingPrice);

        userDao.saveAndFlush(user);


    }

    @PostMapping("/{userAccountNumber}/buy/")
    public void buyStock(@PathVariable String userAccountNumber, @RequestBody Map<String, String> payload) {

        String tempInvestmentId = payload.getOrDefault("investmentId", null);

        User user = this.getUserFromNumber(userAccountNumber);

        this.updateUserInvestments(user);

        Stock stock;
        int quantity;
        double buyPrice = -1 * 1.0;

        if (tempInvestmentId != null) {

            int investmentId = Integer.parseInt(tempInvestmentId);

            Investment investment = this.getInvestmentFromId(user, investmentId);

            if (investment == null)
                throw new IllegalArgumentException("Invalid investment id");

            stock = investment.getStock();

            quantity = Integer.parseInt(payload.getOrDefault("quantity", investment.getQuantity().toString()));

            investment.buy(quantity, buyPrice);

            userDao.saveAndFlush(user);

        } else {
            String stockSymbol = payload.getOrDefault("stockSymbol", null);

            if (stockSymbol == null)
                throw new IllegalArgumentException("No symbol provided");

            try {
                stock = this.stockDataRepo.getStockFromSymbol(stockSymbol);
                quantity = Integer.parseInt(payload.getOrDefault("quantity", "0"));

            } catch (ApiException e) {
                throw new IllegalArgumentException("Invalid stock symbol");
            }
        }

        buyPrice = Double.parseDouble(payload.getOrDefault("buyPrice", stock.getLTP().toString()));

        user.buy(stock, quantity, buyPrice);

    }

}
