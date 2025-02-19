package com.developer.urlshortener.feature.login.entities;

import com.developer.urlshortener.feature.login.domain.AuthProvider;
import com.developer.urlshortener.feature.login.domain.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    private String oauthToken;
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public void setEmail(String email) {

        this.email = email;
    }

    public void setPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void setRole(Role role) {

        this.role = role;
    }

    public void setProvider(AuthProvider authProvider) {

        this.provider = authProvider;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOauthToken(String oauthToken) {

        this.oauthToken = oauthToken;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {

        return password;
    }

    public Role getRole() {

        return role;
    }

    public AuthProvider getProvider() {

        return provider;
    }

    public Long getId() {

        return id;
    }

    public String getOauthToken() {

        return oauthToken;
    }
}
