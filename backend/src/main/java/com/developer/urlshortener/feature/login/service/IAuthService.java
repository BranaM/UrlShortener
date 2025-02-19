package com.developer.urlshortener.feature.login.service;

import com.developer.urlshortener.feature.login.domain.UserDomain;

import java.util.Optional;

public interface IAuthService {

    Optional<UserDomain> registerUser(UserDomain userDomain);

    Optional<UserDomain> loginUser(String email, String password);
}
