package com.developer.urlshortener.feature.login.service;




import com.developer.urlshortener.feature.login.domain.UserDomain;
import com.developer.urlshortener.feature.login.entities.UserEntity;
import com.developer.urlshortener.feature.login.repository.UserRepository;
import com.developer.urlshortener.feature.login.security.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Optional<UserDomain> registerUser(UserDomain userDomain) {
        if (userRepository.existsByEmail(userDomain.getEmail())) {
            return Optional.empty();
        }

        UserEntity userEntity = userDomain.toEntity(passwordEncoder.encode(userDomain.getOauthToken()));
        userEntity = userRepository.save(userEntity);

        String token = jwtTokenUtil.generateToken(userEntity.getEmail());

        UserDomain registeredUser = new UserDomain(userEntity);
        registeredUser.setOauthToken(token);
        return Optional.of(registeredUser);
    }

    @Override
    public Optional<UserDomain> loginUser(String email, String password) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);

        if (optionalUserEntity.isEmpty()) {
            return Optional.empty();
        }

        UserEntity userEntity = optionalUserEntity.get();
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            return Optional.empty();
        }

        String token = jwtTokenUtil.generateToken(email);

        UserDomain loggedInUser = new UserDomain(userEntity);
        loggedInUser.setOauthToken(token);
        return Optional.of(loggedInUser);
    }
}
