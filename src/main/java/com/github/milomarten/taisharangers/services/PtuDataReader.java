package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.models.csv.PTUAbility;
import com.github.milomarten.taisharangers.models.csv.PTUMove;
import com.github.milomarten.taisharangers.models.csv.PTUPokemon;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PtuDataReader {
    @Getter(lazy = true)
    private final List<PTUPokemon> pokemon = readData("pokemon", PTUPokemon.class);
    @Getter(lazy = true)
    private final List<PTUMove> moves = readData("move", PTUMove.class);
    @Getter(lazy = true)
    private final List<PTUAbility> abilities = readData("ability", PTUAbility.class);

    private <T> List<T> readData(String type, Class<T> clazz) {
        try {
            return new CsvToBeanBuilder<T>(new FileReader("src/main/resources/csv/" + type + "-data.csv"))
                    .withType(clazz)
                    .build()
                    .parse();
        } catch (FileNotFoundException e) {
            log.error("Error reading file", e);
            return new ArrayList<>();
        }
    }
}
