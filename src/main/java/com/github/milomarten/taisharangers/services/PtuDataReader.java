package com.github.milomarten.taisharangers.services;

import com.github.milomarten.taisharangers.models.csv.PTUAbility;
import com.github.milomarten.taisharangers.models.csv.PTUMove;
import com.github.milomarten.taisharangers.models.csv.PTUPokemon;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class PtuDataReader {
    private List<PTUPokemon> pokemon = new ArrayList<>();
    private List<PTUMove> moves = new ArrayList<>();
    private List<PTUAbility> abilities = new ArrayList<>();

    @PostConstruct
    private void init() throws Exception {
        this.pokemon = new CsvToBeanBuilder<PTUPokemon>(new FileReader("src/main/resources/csv/pokemon-data.csv"))
                .withType(PTUPokemon.class)
                .build()
                .parse();

        this.moves = new CsvToBeanBuilder<PTUMove>(new FileReader("src/main/resources/csv/move-data.csv"))
                .withType(PTUMove.class)
                .build()
                .parse();

        this.abilities = new CsvToBeanBuilder<PTUAbility>(new FileReader("src/main/resources/csv/abilities-data.csv"))
                .withType(PTUAbility.class)
                .build()
                .parse();
    }
}
