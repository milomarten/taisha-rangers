package com.github.milomarten.taisharangers.models.csv.util;

import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class DashNullStringConverter extends AbstractCsvConverter {
    @Override
    public Object convertToRead(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if ("-".equals(value)) {
            return null;
        }
        return value;
    }
}
