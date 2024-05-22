package com.Brian.JwtAuth.controllers;

import com.Brian.JwtAuth.Dtos.LoginUserDto;
import com.Brian.JwtAuth.Dtos.RegisterUserDto;
import com.Brian.JwtAuth.model.User;
import com.Brian.JwtAuth.services.AuthenticationService;
import com.Brian.JwtAuth.services.JwtService;
import com.Brian.JwtAuth.util.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        try {
            System.out.println("Hello logging start");
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            System.out.println("Hello from authentication");
            String jwtToken = jwtService.generateToken(authenticatedUser);
            System.out.println("token " + jwtToken);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            // Handle invalid credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid credentials"));
        } catch (Exception e) {
            // Handle general exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("An error occurred"));
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<LoginResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid credentials"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<LoginResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("An error occurred"));
    }
}
