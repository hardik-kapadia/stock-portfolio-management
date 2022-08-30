package com.example.stockapi.controller;

import com.example.stockapi.dao.UserDao;
import com.example.stockapi.exception.ApiException;
import com.example.stockapi.model.Investment;
import com.example.stockapi.model.role.ERole;
import com.example.stockapi.model.role.Role;
import com.example.stockapi.model.stock.Stock;
import com.example.stockapi.repository.StockDataRepo;
import com.example.stockapi.model.User;
import com.example.stockapi.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    StockDataRepo stockDataRepo;
    UserDao userDao;

    JwtUtils jwtUtils;

    @Autowired
    public UserController(StockDataRepo stockDataRepo, UserDao userDao, JwtUtils jwtUtils) {
        this.stockDataRepo = stockDataRepo;
        this.userDao = userDao;
        this.jwtUtils = jwtUtils;
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

        user.refresh();
    }

    @GetMapping("/refreshUser")
    public void refreshUserInvestments(HttpServletRequest httpServletRequest) {
        User u = this.userDao.getUserByEmail(this.jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(httpServletRequest))).orElseThrow(IllegalArgumentException::new);
        updateUserInvestments(u);
    }

    @GetMapping("/checkRole")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    private ResponseEntity<Boolean> hasAuthority(HttpServletRequest httpServletRequest) {

        User u = this.userDao.getUserByEmail(this.jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(httpServletRequest))).orElseThrow(IllegalArgumentException::new);

        System.out.println("User: " + u);

        return ResponseEntity.ok(u.getRoles().contains(new Role(ERole.ROLE_USER)));

    }

    private Optional<Investment> getInvestmentFromId(User user, Integer investmentId) {
        Investment investment = null;

        List<Investment> userInvestments = user.getInvestments();

        System.out.println("User: " + user + " -> Updating investments: " + userInvestments);


        for (Investment userInvestment : userInvestments) {
            if (userInvestment.getId().intValue() == investmentId.intValue()) {
                investment = userInvestment;
                break;
            }
        }

        return Optional.ofNullable(investment);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return this.userDao.findAll();
    }

    @GetMapping("/investments")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Investment> getUserInvestments(HttpServletRequest httpServletRequest) {

        User user = this.userDao.getUserByEmail(this.jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(httpServletRequest))).orElseThrow(IllegalArgumentException::new);

        this.updateUserInvestments(user);

        return user.getInvestments();
    }

    @GetMapping("/info")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public User getUserInfo(HttpServletRequest httpServletRequest) {

        User user = this.userDao.getUserByEmail(this.jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(httpServletRequest))).orElseThrow(IllegalArgumentException::new);

        this.updateUserInvestments(user);

        return user;

    }

    @PostMapping("/sell")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> sellStock(HttpServletRequest httpServletRequest, @RequestBody Map<String, String> payload) {

        String tempInvestmentId = payload.getOrDefault("investmentId", null);
        String stockSymbol = payload.getOrDefault("stockSymbol", null);

        if (tempInvestmentId == null && stockSymbol == null) {
            throw new IllegalArgumentException("No investment or stockSymbol is provided");
        }
        User user = this.userDao.getUserByEmail(this.jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(httpServletRequest))).orElseThrow(IllegalArgumentException::new);

        this.updateUserInvestments(user);

        Optional<Investment> optionalInvestment = tempInvestmentId == null ? user.getInvestmentFromStock(stockSymbol) : user.getInvestmentFromId(Integer.parseInt(tempInvestmentId));

        if (optionalInvestment.isPresent()) {

            Investment investment = optionalInvestment.get();

            int quantity = Integer.parseInt(payload.getOrDefault("quantity", investment.getQuantity().toString()));

            Double sellingPrice = Double.parseDouble(payload.getOrDefault("sellingPrice", investment.getStock().getLTP().toString()));

            user.sell(investment, quantity, sellingPrice);

        } else
            return ResponseEntity.badRequest().body("Invalid investment Id or stock Symbol ");

        userDao.saveAndFlush(user);

        return ResponseEntity.ok("Stock purchased successfully");

    }

    @PostMapping("/buy")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> buyStock(HttpServletRequest httpServletRequest, @RequestBody Map<String, String> payload) {

        String tempInvestmentId = payload.getOrDefault("investmentId", null);

        User user = this.userDao.getUserByEmail(this.jwtUtils.getUserNameFromJwtToken(this.jwtUtils.getJwtFromCookies(httpServletRequest))).orElseThrow(IllegalArgumentException::new);

        this.updateUserInvestments(user);

        Stock stock;
        int quantity;
        double buyPrice;

        if (tempInvestmentId != null) {

            int investmentId = Integer.parseInt(tempInvestmentId);

            Optional<Investment> optionalInvestment = this.getInvestmentFromId(user, investmentId);

            if (optionalInvestment.isEmpty())
                return ResponseEntity.badRequest().body("Invalid investment Id");

            Investment investment = optionalInvestment.get();

            stock = investment.getStock();

            quantity = Integer.parseInt(payload.getOrDefault("quantity", investment.getQuantity().toString()));


        } else {
            String stockSymbol = payload.getOrDefault("stockSymbol", null);

            if (stockSymbol == null)
                return ResponseEntity.badRequest().body("stock symbol/ investment Id required");

            try {
                stock = this.stockDataRepo.getStockFromSymbol(stockSymbol);
                quantity = Integer.parseInt(payload.getOrDefault("quantity", "0"));

                System.out.println("Successfully set: " + stock + " with q: " + quantity);

            } catch (ApiException e) {
                return ResponseEntity.badRequest().body("stock symbol/ investment Id required");
            }
        }

        buyPrice = Double.parseDouble(payload.getOrDefault("buyPrice", stock.getLTP().toString()));

        user.buy(stock, quantity, buyPrice);

        userDao.saveAndFlush(user);

        return ResponseEntity.ok("Stock purchased successfully");
    }

}
