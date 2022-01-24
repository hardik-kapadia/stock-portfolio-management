package com.example.stockapi.controller;

import com.example.stockapi.dao.UserDao;
import com.example.stockapi.exception.ApiException;
import com.example.stockapi.model.Investment;
import com.example.stockapi.model.stock.Stock;
import com.example.stockapi.repository.StockDataRepo;
import com.example.stockapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    private User getAuthenticatedUser() {
        return null;
    }

    private void updateUserInvestments(User user) {
        user.getInvestments().forEach(i -> {
            try {
                this.stockDataRepo.updateStock(i.getStock());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        });

        user.refresh();
    }

    private Investment getInvestmentFromId(User user, Integer investmentId) {
        Investment investment = null;

        List<Investment> userInvestments = user.getInvestments();

        System.out.println("User: " + user + " -> Updating investments: " + userInvestments);


        for (Investment userInvestment : userInvestments) {
            if (userInvestment.getId().intValue() == investmentId.intValue()) {
                investment = userInvestment;
                break;
            }
        }

        return investment;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return this.userDao.findAll();
    }

    @GetMapping("/{userAccountNumber}/investments")
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

    @PostMapping("/{userAccountNumber}/sell")
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
        user.refresh();

        userDao.saveAndFlush(user);

    }

    @PostMapping("/{userAccountNumber}/buy")
    public ResponseEntity<String> buyStock(@PathVariable String userAccountNumber, @RequestBody Map<String, String> payload) {

        System.out.println("buying for: User account number: " + userAccountNumber);

        String tempInvestmentId = payload.getOrDefault("investmentId", null);

        User user = this.getUserFromNumber(userAccountNumber);

        this.updateUserInvestments(user);

        Stock stock;
        int quantity;
        double buyPrice;

        if (tempInvestmentId != null) {

            int investmentId = Integer.parseInt(tempInvestmentId);

            Investment investment = this.getInvestmentFromId(user, investmentId);

            if (investment == null)
                return ResponseEntity.badRequest().body("Invalid investment Id");

            stock = investment.getStock();

            quantity = Integer.parseInt(payload.getOrDefault("quantity", investment.getQuantity().toString()));


        } else {
            String stockSymbol = payload.getOrDefault("stockSymbol", null);

            if (stockSymbol == null)
                return ResponseEntity.badRequest().body("stock symbol/ investment Id required");

            try {
                stock = this.stockDataRepo.getStockFromSymbol(stockSymbol);
                quantity = Integer.parseInt(payload.getOrDefault("quantity", "0"));

                System.out.println("Succesfully set: " + stock + " with q: " + quantity);

            } catch (ApiException e) {
                return ResponseEntity.badRequest().body("stock symbol/ investment Id required");
            }
        }

        buyPrice = Double.parseDouble(payload.getOrDefault("buyPrice", stock.getLTP().toString()));

        user.buy(stock, quantity, buyPrice);

        userDao.saveAndFlush(user);

        return ResponseEntity.ok("Stock purchased successfully");
    }

    @PostMapping("/add")
    public void addUser(@RequestBody User user) {
//        User u = new User(user.getName(), user.getEmail(), user.getMobileNumber(), user.getPassword(),user.getAccountNumber());
        System.out.println("User rcvd: "+user);
        userDao.saveAndFlush(user);
    }

    @PostMapping("/delete")
    public void deleteUser(@RequestParam String userAccountNumber) {
        userDao.deleteById(userAccountNumber);
    }

}
