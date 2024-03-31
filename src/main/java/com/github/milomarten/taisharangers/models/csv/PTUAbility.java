package com.github.milomarten.taisharangers.models.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class PTUAbility {
    @CsvBindByName(column = "Name")
    private String name;
    @CsvBindByName(column = "Frequency")
    private String frequency;
    @CsvBindByName(column = "Effect")
    private String effect;
    @CsvBindByName(column = "Trigger")
    private String trigger;
    @CsvBindByName(column = "Target")
    private String target;
    @CsvBindByName(column = "Keywords")
    private String keywords;
    @CsvBindByName(column = "Effect 2")
    private String effect2;
}
