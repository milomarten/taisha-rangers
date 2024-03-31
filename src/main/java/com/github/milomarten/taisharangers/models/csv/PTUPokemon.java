package com.github.milomarten.taisharangers.models.csv;

import com.github.milomarten.taisharangers.models.csv.util.DashNullStringConverter;
import com.github.milomarten.taisharangers.models.csv.util.NonNullMultiValuedMap;
import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.List;

@Data
public class PTUPokemon {
    @CsvBindByName(column = "Pokemon:")
    private String name;
    @CsvBindByName(column = "HP")
    private int hp;
    @CsvBindByName(column = "Attack")
    private int attack;
    @CsvBindByName(column = "Defense")
    private int defense;
    @CsvBindByName(column = "Special Attack")
    private int specialAttack;
    @CsvBindByName(column = "Special Defense")
    private int specialDefense;
    @CsvBindByName(column = "Type 1")
    private PTUType primaryType;
    @CsvBindByName(column = "Type 2")
    private PTUType secondaryType;

    // Capabilities
    @CsvBindByName(column = "Overland")
    private int overland;
    @CsvBindByName(column = "Sky")
    private int sky;
    @CsvBindByName(column = "Swim")
    private int swim;
    @CsvBindByName(column = "Levitate")
    private int levitate;
    @CsvBindByName(column = "Burrow")
    private int burrow;
    @CsvBindByName(column = "H Jump")
    private int highJump;
    @CsvBindByName(column = "L Jump")
    private int longJump;
    @CsvBindByName(column = "Power")
    private int power;

    @CsvBindAndSplitByName(column = "Naturewalk", elementType = String.class, splitOn = ", ")
    private List<String> naturewalk;

    @CsvBindAndJoinByName(column = "Capability [1-9]", elementType = String.class, converter = DashNullStringConverter.class, mapType = NonNullMultiValuedMap.class)
    private MultiValuedMap<String, String> capabilities;

    @CsvBindByName(column = "Size")
    private PTUSize size;
    @CsvBindByName(column = "Weight")
    private int weight;

    @CsvBindAndJoinByName(column = "EggGroup[1-9]", elementType = String.class, converter = DashNullStringConverter.class, mapType = NonNullMultiValuedMap.class)
    private MultiValuedMap<String, String> eggGroups;
}
