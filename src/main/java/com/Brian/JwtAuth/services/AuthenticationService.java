package com.Brian.JwtAuth.services;

import com.Brian.JwtAuth.Dtos.LoginUserDto;
import com.Brian.JwtAuth.Dtos.RegisterUserDto;
import com.Brian.JwtAuth.model.User;
import com.Brian.JwtAuth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User signup(RegisterUserDto data) {
        User user = new User();
        user.setFullName(data.getFullName());
        user.setEmail(data.getEmail());
        user.setPassword(passwordEncoder.encode(data.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto data) {
        System.out.println("authenticate");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        data.getEmail(),
                        data.getPassword()
                )
        );
        System.out.println("done");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return userRepository.findByEmail(data.getEmail())
                .orElseThrow(()-> new RuntimeException("user not found"));
    }
}
