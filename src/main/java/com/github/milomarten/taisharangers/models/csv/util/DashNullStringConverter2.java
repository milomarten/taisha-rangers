package com.github.milomarten.taisharangers.models.csv.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class DashNullStringConverter2 extends AbstractBeanField<Object, Object> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if ("--".equals(value)) {
            return null;
        }
        return value;
    }
}
