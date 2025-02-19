package com.developer.urlshortener.feature.urlGenerator.rest;

import com.developer.urlshortener.feature.urlGenerator.domain.UrlDomain;
import com.developer.urlshortener.feature.urlGenerator.entities.UrlEntity;
import com.developer.urlshortener.feature.urlGenerator.service.IUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/urls")
public class UrlController {
    private final IUrlService urlService;

    @Autowired
    public UrlController(IUrlService urlService){
        this.urlService = urlService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UrlDomain>> getAllUrls(){
        List<UrlDomain> urls = urlService.findAllUrls();
        if(urls.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return  ResponseEntity.ok(urls);
    }

    @GetMapping("/short/{shortUrl}")
    public ResponseEntity<UrlDomain> getUrlByShortUrl(@PathVariable String shortUrl){
        Optional<UrlDomain> url = urlService.findByShortUrl(shortUrl);
        return url.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UrlDomain> getUrlById(@PathVariable Long id){
        Optional<UrlDomain> url = urlService.findById(id);
        return url.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/redirect/{shortUrl}")
    public ResponseEntity<Void> redirectToOirginal(@PathVariable String shortUrl){
        String originalUrl = urlService.redirectToOriginalUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {
        if (urlService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        urlService.deleteUrl(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlDomain> generateShortUrl(@RequestBody String originalUrl){
        try{
            //napravimo taj entity
            Optional<UrlDomain> urlDomainOptional = urlService.createShortUrl(originalUrl);

            return urlDomainOptional
                    .map(urlDomain -> ResponseEntity.status(HttpStatus.CREATED).body(urlDomain))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
            //pa njega map u dto obj
            //i onda sacuvamo
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
