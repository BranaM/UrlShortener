package com.developer.urlshortener.feature.urlGenerator.domain;

import com.developer.urlshortener.feature.urlGenerator.entities.UrlEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UrlDomain {
    private Long id;
    private String originalUrl;
    private String shortUrl;

    public UrlDomain(UrlEntity urlEntity){
        id = urlEntity.getId();
        originalUrl = urlEntity.getOriginalUrl();
        shortUrl = urlEntity.getShortUrl();
    }
}
