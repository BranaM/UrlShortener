package com.developer.urlshortener.feature.urlGenerator.service;

import com.developer.urlshortener.feature.urlGenerator.domain.UrlDomain;
import com.developer.urlshortener.feature.urlGenerator.entities.UrlEntity;
import com.developer.urlshortener.feature.urlGenerator.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    private UrlEntity urlEntity;

    @BeforeEach
    void setUp() {
        urlEntity = UrlEntity.builder()
                .id(1L)
                .originalUrl("https://example.com")
                .shortUrl("abc123")
                .userId(1)
                .build();
    }

    @Test
    void testGetAllUrls() {
        UrlEntity urlEntity2 = UrlEntity.builder()
                .originalUrl("https://another.com")
                .shortUrl("xyz789")
                .userId(2)
                .build();

        List<UrlEntity> urlEntities = Arrays.asList(urlEntity, urlEntity2);
        when(urlRepository.findAll()).thenReturn(urlEntities);

        List<UrlDomain> result = urlService.findAllUrls();
        assertEquals(2, result.size());
        assertEquals("https://example.com", result.get(0).getOriginalUrl());
        assertEquals("https://another.com", result.get(1).getOriginalUrl());
    }

    @Test
    void testFindByShortUrl() {
        when(urlRepository.findByShortUrl("abc123")).thenReturn(Optional.of(urlEntity));

        Optional<UrlDomain> result = urlService.findByShortUrl("abc123");
        assertTrue(result.isPresent());
        assertEquals("abc123", result.get().getShortUrl());
    }

    @Test
    void testFindById() {
        when(urlRepository.findById(1L)).thenReturn(Optional.of(urlEntity));

        Optional<UrlDomain> result = urlService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("https://example.com", result.get().getOriginalUrl());
    }

    @Test
    void testRedirectToOriginalUrl() {
        when(urlRepository.findByShortUrl("abc123")).thenReturn(Optional.of(urlEntity));

        String originalUrl = urlService.redirectToOriginalUrl("abc123");
        assertEquals("https://example.com", originalUrl);
    }

    @Test
    void testRedirectToOriginalUrl_NotFound() {
        when(urlRepository.findByShortUrl("notfound")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> urlService.redirectToOriginalUrl("notfound"));
        assertEquals("Short URL not found", exception.getMessage());
    }

    @Test
    void testDeleteUrl() {
        Long urlId = 1L;
        when(urlRepository.findById(urlId)).thenReturn(Optional.of(urlEntity));

        urlService.deleteUrl(urlId);

        verify(urlRepository, times(1)).deleteById(urlId);
    }

    @Test
    void testCreateShortUrl() {
        when(urlRepository.findByShortUrl(anyString())).thenReturn(Optional.empty());
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(urlEntity);

        Optional<UrlDomain> result = urlService.createShortUrl("https://example.com");
        assertTrue(result.isPresent());
        assertEquals("https://example.com", result.get().getOriginalUrl());
    }

    @Test
    void testGenerateShortUrl() {
        when(urlRepository.findByShortUrl(anyString())).thenReturn(Optional.empty());

        String shortUrl = urlService.generateShortUrl();
        assertNotNull(shortUrl);
        assertEquals(6, shortUrl.length());
    }

    @Test
    void testGenerateShortUrl_RetryLimitExceeded() {
        when(urlRepository.findByShortUrl(anyString())).thenReturn(Optional.of(new UrlEntity()));

        Exception exception = assertThrows(RuntimeException.class, () -> urlService.generateShortUrl());
        assertEquals("Unable to generate a unique short URL after 10 attempts", exception.getMessage());
    }

}
