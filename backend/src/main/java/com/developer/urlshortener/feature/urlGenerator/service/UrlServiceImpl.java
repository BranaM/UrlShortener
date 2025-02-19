package com.developer.urlshortener.feature.urlGenerator.service;

import com.developer.urlshortener.feature.urlGenerator.domain.UrlDomain;
import com.developer.urlshortener.feature.urlGenerator.entities.UrlEntity;
import com.developer.urlshortener.feature.urlGenerator.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UrlServiceImpl implements IUrlService{

    private final UrlRepository urlRepository;
    private static final String BASE62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository){
        this.urlRepository = urlRepository;
    }

    @Override
    public String generateBase62(int length) {
        StringBuilder shortUrl = new StringBuilder();
        for(int i=0; i<length; i++){
            int index = RANDOM.nextInt(BASE62_CHARACTERS.length());
            shortUrl.append(BASE62_CHARACTERS.charAt(index));
        }
        return shortUrl.toString();
    }

    @Override
    public String generateShortUrl() {
        int retryLimit = 10;
        int attempts = 0;

        while (attempts < retryLimit) {
            String shortUrl = generateBase62(6);

            Optional<UrlEntity> existingUrl = urlRepository.findByShortUrl(shortUrl);
            if (!existingUrl.isPresent()) {
                return shortUrl;
            }
            attempts++;
        }
        throw new RuntimeException("Unable to generate a unique short URL after " + retryLimit + " attempts");
    }

    @Override
    public Optional<UrlDomain> createShortUrl(String originalUrl) {
        String shortUrl = generateShortUrl();

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setOriginalUrl(originalUrl);
        urlEntity.setShortUrl(shortUrl);

        UrlEntity savedEntity = urlRepository.save(urlEntity);
        return Optional.of(new UrlDomain(savedEntity));
    }


    @Override
    public List<UrlDomain> findAllUrls() {
        return urlRepository.findAll().stream().map(UrlDomain::new).collect(Collectors.toList());
    }

    @Override
    public Optional<UrlDomain> findById(Long id) {
        return urlRepository.findById(id).map(UrlDomain::new);
    }

    @Override
    public Optional<UrlDomain> findByShortUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl).map(UrlDomain::new);
    }

    @Override
    public void deleteUrl(Long id) {

        //urlRepository.findById(id).ifPresent(urlRepository::delete);
        urlRepository.findById(id).ifPresent(entity -> urlRepository.deleteById(entity.getId()));
    }

    @Override
    public String redirectToOriginalUrl(String shortUrl) {
        //UrlDomain url = urlRepository.findByShortUrl(shortUrl).map(UrlDomain::new)
        //.orElseThrow(() -> new RuntimeException("Short URL not found"));
        //return url.getOriginalUrl();
        return urlRepository.findByShortUrl(shortUrl)
                .map(UrlDomain::new)
                .map(UrlDomain::getOriginalUrl)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
    }
}
