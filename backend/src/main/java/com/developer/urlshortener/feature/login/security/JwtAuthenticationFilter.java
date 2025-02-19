package com.developer.urlshortener.feature.login.security;

import com.developer.urlshortener.feature.login.domain.UserDomain;
import com.developer.urlshortener.feature.login.entities.UserEntity;
import com.developer.urlshortener.feature.login.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Proveravamo da li Authorization header postoji i da li počinje sa "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Ekstraktujemo token
        String token = authHeader.substring(7);
        String email = jwtTokenUtil.extractUsername(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Dobijamo UserDomain pomoću email-a
            Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

            if (userEntityOptional.isPresent() && jwtTokenUtil.validateToken(token)) {
                UserEntity userEntity = userEntityOptional.get();
                UserDomain userDomain = new UserDomain(userEntity);

                // Kreiramo UsernamePasswordAuthenticationToken bez authorities
                String role = "ROLE_" + userDomain.getRole(); // Pretpostavljam da `getRole()` vraća "USER" ili "ADMIN"
                GrantedAuthority authority = new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDomain, null, Collections.singleton(authority));

                // Postavljamo autentifikaciju u SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Nastavljamo sa filter lancem
        chain.doFilter(request, response);
    }
}
