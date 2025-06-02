 package com.Wok.Wok.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.Wok.Wok.Model.AuthRequest;
import com.Wok.Wok.Model.AuthResponse;
import com.Wok.Wok.Model.user;
import com.Wok.Wok.Repository.UserRepository;
import com.Wok.Wok.Services.CustomUserDetailService;
import com.Wok.Wok.Services.JwtService;

import jakarta.security.auth.message.callback.PrivateKeyCallback.AliasRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.webauthn.api.AuthenticatorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@SuppressWarnings("unused")
@RestController
@RequestMapping("api")
public class LoginController {
    @Autowired
    private  AuthenticationProvider authenticationProvider;
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    LoginController(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

   
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody user newUser) {
        // Check if username already exists
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        // Encode password and save new user
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user savedUser = userRepository.save(newUser);
        
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/user")
    public ResponseEntity<user> user(@RequestParam String userId) {
        // This endpoint is accessible to authenticated users
        Optional<user> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        }
        return ResponseEntity.notFound().build();
        
    }

    



    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        try {
            System.out.println("Username: " + request.getUsername());
            System.out.println("Password: " + request.getPassword());

            // Authenticate the user using the provided username and password
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Retrieve user from the database
            Optional<user> user = userRepository.findByUsername(request.getUsername());

            if (user.isPresent()) {
                // Convert roles from List<String> to List<GrantedAuthority>
                List<SimpleGrantedAuthority> authorities = user.get().getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

                // Create UserDetails and generate JWT
                UserDetails userDetails = new User(
                        user.get().getUsername(),
                        user.get().getPassword(),
                        authorities
                );

                String token = jwtService.generateToken(userDetails);

                return ResponseEntity.ok(new AuthResponse(token));
            }

            // Username doesn't exist
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");

        } catch (BadCredentialsException e) {
            // Authentication failed (e.g., bad password)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        } catch (Exception e) {
            // Other errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong: " + e.getMessage());
        }
    }

    
    
}
