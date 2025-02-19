package com.developer.urlshortener.feature.login.rest;


import com.developer.urlshortener.feature.login.domain.UserDomain;
import com.developer.urlshortener.feature.login.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final IAuthService authService;

    @Autowired
    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDomain> register(@RequestBody UserDomain userDomain) {
        Optional<UserDomain> registeredUser = authService.registerUser(userDomain);

        return registeredUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("/login")
    public ResponseEntity<UserDomain> login(@RequestBody UserDomain userDomain) {
        Optional<UserDomain> loggedInUser = authService.loginUser(userDomain.getEmail(), userDomain.getOauthToken());

        return loggedInUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

}
