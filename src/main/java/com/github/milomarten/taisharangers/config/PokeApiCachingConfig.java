package com.github.milomarten.taisharangers.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import skaro.pokeapi.PokeApiReactorCachingConfiguration;

import java.time.Duration;

@Configuration
@Import(PokeApiReactorCachingConfiguration.class)
@EnableCaching
public class PokeApiCachingConfig {
    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("Auto refresh & no connection limit")
                .maxIdleTime(Duration.ofSeconds(10))
                .maxConnections(500)
                .pendingAcquireMaxCount(-1)
                .build();
    }

    @Bean
    public HttpClient httpClient(ConnectionProvider connectionProvider) {
        return HttpClient.create(connectionProvider)
                .compress(true)
                .resolver(DefaultAddressResolverGroup.INSTANCE);
    }

    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        cacheManager.setAllowNullValues(true);
        cacheManager.setAsyncCacheMode(true);
        return cacheManager;
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(50)
                .weakKeys()
                .recordStats();
    }
}
