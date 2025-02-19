package com.developer.urlshortener.feature.login.security;

import com.developer.urlshortener.feature.login.handler.OAuth2AuthenticationSuccessHandler;
import com.developer.urlshortener.feature.login.service.OauthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler; // Injektuj success handler

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oauth2AuthenticationSuccessHandler = oauth2AuthenticationSuccessHandler; // Postavi instancu
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OauthService oauthService) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Onemogućavamo CSRF
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sesije
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v2/auth/login", "/v2/auth/register", "/oauth2/**").permitAll() // Endpointi za autentifikaciju (login/signup) su slobodni
                        .anyRequest().authenticated() // Svi ostali endpointi zahtevaju autentifikaciju
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauthService))  // Koristi custom servis za korisnike
                        .successHandler(oauth2AuthenticationSuccessHandler) // Generiše JWT token nakon uspešne prijave
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Dodajemo naš filter

        return http.build();
    }
}
