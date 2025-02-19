package com.developer.urlshortener.feature.login.service;

import com.developer.urlshortener.feature.login.domain.AuthProvider;
import com.developer.urlshortener.feature.login.domain.Role;
import com.developer.urlshortener.feature.login.domain.UserDomain;
import com.developer.urlshortener.feature.login.entities.UserEntity;
import com.developer.urlshortener.feature.login.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class OauthService extends DefaultOAuth2UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public void CustomOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public OauthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");

        Optional<UserEntity> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            String encodedPassword = passwordEncoder.encode("deafult_password");

            // Kreiramo novi UserDomain umesto UserEntity
            UserDomain newUserDomain = UserDomain.builder()
                    .email(email)
                    .role(Role.USER)
                    .authProvider(AuthProvider.GOOGLE)
                    .oauthToken("generated_oauth_token")
                    .build();

            UserEntity savedUser = userRepository.save(newUserDomain.toEntity(passwordEncoder.encode("default_password")));
            return new DefaultOAuth2User(
                    oAuth2User.getAuthorities(),
                    Map.of("email", savedUser.getEmail(), "role", savedUser.getRole().toString()),
                    "email"
            );
        }

        // Možda vraćamo oAuth2User sa UserDomain podacima
        return oAuth2User;
    }


}
