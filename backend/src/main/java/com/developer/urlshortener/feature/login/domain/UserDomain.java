package com.developer.urlshortener.feature.login.domain;

import com.developer.urlshortener.feature.login.entities.UserEntity;
import lombok.*;
import lombok.Builder;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDomain {
    private Long id;
    private String email;
    private Role role;
    private AuthProvider authProvider;
    private String oauthToken;

    public UserDomain(UserEntity userEntity){
        id = userEntity.getId();
        email = userEntity.getEmail();
        role = userEntity.getRole();
        authProvider = userEntity.getProvider();
        oauthToken = userEntity.getOauthToken();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public UserEntity toEntity(String encodedPassword) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(this.id);
        userEntity.setEmail(this.email);
        userEntity.setRole(this.role);
        userEntity.setProvider(this.authProvider);
        userEntity.setOauthToken(this.oauthToken);
        userEntity.setPassword(encodedPassword);
        return userEntity;
    }
}
