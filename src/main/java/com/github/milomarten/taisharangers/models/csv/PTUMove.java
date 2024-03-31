package com.github.milomarten.taisharangers.models.csv;

import com.github.milomarten.taisharangers.models.csv.util.DashNullStringConverter2;
import com.github.milomarten.taisharangers.models.csv.util.OToFlagConverter;
import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Data;
import org.apache.commons.collections4.MultiValuedMap;

@Data
public class PTUMove {
    @CsvBindByName(column = "Name")
    private String name;
    @CsvBindByName(column = "Type")
    private PTUType type;
    @CsvBindByName(column = "Category")
    private PTUCategory category;
    @CsvCustomBindByName(column = "Damage base", converter = DashNullStringConverter2.class)
    private String damageBase;
    @CsvBindByName(column = "Frequency")
    private String frequency;
    @CsvCustomBindByName(column = "AC", converter = DashNullStringConverter2.class)
    private String accuracyCheck;
    @CsvBindByName(column = "Range")
    private String range;
    @CsvBindByName(column = "Effects")
    private String effects;
    @CsvBindByName(column = "Contest Stats")
    private String contestStats;

    // Flags
    // Sheer Force,Tough Claws,Technician,Reckless,Iron Fist,Mega Launcher,Mega Launcher [Playtest],Punk Rock,Strong Jaw,Reckless [Playtest]
    @CsvBindAndJoinByName(column = ".*", elementType = Boolean.class, converter = OToFlagConverter.class)
    private MultiValuedMap<String, Boolean> abilityFlags;
}
