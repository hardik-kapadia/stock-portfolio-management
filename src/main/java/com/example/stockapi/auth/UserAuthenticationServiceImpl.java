package com.example.stockapi.auth;

import com.example.stockapi.dao.UserDao;
import com.example.stockapi.model.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAuthenticationServiceImpl implements UserAuthenticationService{

    UserDao userDao;

    @Override
    public Optional<String> login(String username, String password) {

        Optional<User> tempUser = Optional.ofNullable(userDao.getUserByEmail(username));

        if(tempUser.isPresent()){
            User user = tempUser.get();
            if(user.getPassword().equals(password))
                return Optional.of(user.getAccountNumber());

            throw new BadCredentialsException("Incorrect Password");
        }

        throw new BadCredentialsException("Invalid username");

    }

    @Override
    public Optional<User> findByToken(String token) {

        //TODO : complete
        return Optional.empty();

    }

    @Override
    public void logout(User user) {

    }
}
