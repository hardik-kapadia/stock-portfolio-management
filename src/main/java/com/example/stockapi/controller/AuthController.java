package com.example.stockapi.controller;

import com.example.stockapi.dao.RoleDao;
import com.example.stockapi.dao.UserDao;
import com.example.stockapi.model.User;
import com.example.stockapi.model.role.ERole;
import com.example.stockapi.model.role.Role;
import com.example.stockapi.security.jwt.JwtUtils;
import com.example.stockapi.security.services.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDao userRepository;

    @Autowired
    RoleDao roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping(value = "/signin",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> payload) throws JsonProcessingException {

        if (!payload.containsKey("username"))
            return ResponseEntity.badRequest().body("No username provided");

        if (!payload.containsKey("password"))
            return ResponseEntity.badRequest().body("No password provided");

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(payload.get("username"), payload.get("password")));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        Map<String,String> responsePayload = new HashMap<>();
        responsePayload.put("id",userDetails.getId().toString());
        responsePayload.put("email",userDetails.getEmail());
        responsePayload.put("roles",roles.toString());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(this.objectMapper.writeValueAsString(responsePayload));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody Map<String, String> payload) {

        if (!payload.containsKey("username"))
            return ResponseEntity.badRequest().body("No username provided");

        if (!payload.containsKey("password"))
            return ResponseEntity.badRequest().body("No password provided");

        if (userRepository.existsByEmail(payload.get("username")))
            return ResponseEntity.badRequest().body("Error: Email is already in use!");

        if (!payload.containsKey("name"))
            return ResponseEntity.badRequest().body("No Name provided");

        if (!payload.containsKey("accountnumber"))
            return ResponseEntity.badRequest().body("No accountNumber provided");

        if (userRepository.existsByAccountNumber(payload.get("accountNumber")))
            return ResponseEntity.badRequest().body("Error: Account Number is already in use!");

        if (!payload.containsKey("mobilenumber"))
            return ResponseEntity.badRequest().body("No mobile number provided");

        if (userRepository.existsByMobileNumber(payload.get("mobilenumber")))
            return ResponseEntity.badRequest().body("Error: Mobile Number is already in use!");

        Set<Role> roles = new HashSet<>();

        String role = payload.getOrDefault("roles", "[USER]");

        String[] roleIndividual = role.substring(1, role.length() - 1).split(",");

        for (String s : roleIndividual) {
            s = s.toUpperCase();

            switch (s) {
                case "USER" -> {

                    Role userTempRole;


                    try {
                        userTempRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("No such role"));
                    } catch (RuntimeException re) {
                        roleRepository.save(new Role(ERole.ROLE_USER));
                        roleRepository.flush();
                        userTempRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
                    }

                    roles.add(userTempRole);

                }
                case "ADMIN" -> {
                    Role adminTempRole;
                    try {
                        adminTempRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("No such role"));
                    } catch (RuntimeException re) {
                        roleRepository.save(new Role(ERole.ROLE_ADMIN));
                        roleRepository.flush();
                        adminTempRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
                    }

                    roles.add(adminTempRole);
                }
                default -> throw new RuntimeException("No such role");
            }
        }


        // Create new user's account
        User user = new User(payload.get("name"), payload.get("email"),
                payload.get("mobilenumber"), encoder.encode(payload.get("password")), payload.get("accountnumber"));

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You've been signed out!");
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> deleteUser(HttpServletRequest httpServletRequest, @Valid @RequestBody Map<String, String> payload) {

        if (!payload.containsKey("password"))
            return ResponseEntity.badRequest().body("No password provided");

        String p = encoder.encode(payload.get("password"));

        User u = userRepository.getUserByEmail(jwtUtils.getUserNameFromJwtToken(jwtUtils.getJwtFromCookies(httpServletRequest))).orElseThrow();

        if (!p.equals(u.getPassword()))
            return ResponseEntity.badRequest().body("Incorrect password");

        userRepository.delete(u);

        return ResponseEntity.ok("User with username: " + u.getEmail() + " was deleted successfully");
    }
}
