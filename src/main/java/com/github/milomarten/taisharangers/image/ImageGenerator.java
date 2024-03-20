package com.github.milomarten.taisharangers.image;

import com.github.milomarten.taisharangers.image.effects.Effects;
import com.github.milomarten.taisharangers.services.TokenGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import skaro.pokeapi.client.PokeApiClient;
import skaro.pokeapi.query.PageQuery;
import skaro.pokeapi.resource.NamedApiResourceList;
import skaro.pokeapi.resource.pokemon.Pokemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@Slf4j
public class ImageGenerator implements ApplicationRunner {
    private static final TokenGeneratorService.CustomizationOptions NORMAL = TokenGeneratorService.CustomizationOptions.builder().build();
    private static final TokenGeneratorService.CustomizationOptions SHADOW = TokenGeneratorService.CustomizationOptions.builder()
            .effect(Effects.SHADOW)
            .build();
    @Autowired
    private PokeApiClient client;

    @Autowired
    private TokenGeneratorService tokenGeneratorService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File tokensFile = new File("tokens.zip");
        if (tokensFile.exists()) {
            log.info("tokens.zip already exists, so tokens will not be generated.");
            return;
        }
        log.info("Creating default tokens for all Pokemon.");
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(tokensFile));
        client.getResource(Pokemon.class, new PageQuery(100_000, 0))
                .flatMapIterable(NamedApiResourceList::getResults)
                .flatMap(nam -> client.followResource(() -> nam, Pokemon.class))
                .flatMap(pkmn -> {
                    String name = pkmn.getName();
                    var img = tokenGeneratorService.generateToken(pkmn, NORMAL);
                    var shadow_img = tokenGeneratorService.generateToken(pkmn, SHADOW);
                    if (img == null || shadow_img == null) {
                        log.error("Encountered a weird guy: {}", name);
                        return Mono.empty();
                    }
                    try {
                        zipOut.putNextEntry(new ZipEntry(name + ".png"));
                        zipOut.write(img.toBytes());
                        zipOut.putNextEntry(new ZipEntry(name + "_shadow.png"));
                        zipOut.write(shadow_img.toBytes());
                        return Mono.just(pkmn);
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                })
                .doOnComplete(() -> {
                    try {
                        zipOut.close();
                        log.info("Completed token generation. Saved to {}", tokensFile.getCanonicalPath());
                    } catch (IOException e) {
                        throw new RuntimeException("Error closing zip file", e);
                    }
                })
                .subscribe();
    }
}
