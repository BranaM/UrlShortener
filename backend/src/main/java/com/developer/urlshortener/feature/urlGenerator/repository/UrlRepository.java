package com.developer.urlshortener.feature.urlGenerator.repository;

import com.developer.urlshortener.feature.urlGenerator.entities.UrlEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> findByShortUrl(String shortUrl);

    @Override
    @NonNull
    Optional<UrlEntity> findById(@NonNull Long urlId);
}
