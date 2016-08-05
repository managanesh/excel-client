package com.deere.isg.tx.excel.processor.helpers;

import com.deere.isg.tx.excel.exception.ExcelProcessorException;
import com.deere.isg.tx.excel.processor.ExcelProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.NumberUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Date;

public class ReflectionHelper<T> {
    private final ExcelProcessor<T> excelProcessor;

    public ReflectionHelper(ExcelProcessor<T> excelProcessor) {
        this.excelProcessor = excelProcessor;
    }

    public void setValue(T dataRow, Object val, String fieldName) {

        Method method = BeanUtils.findMethodWithMinimalParameters(excelProcessor.getBeanClass(), "set" + fieldName);
        try {

            val = convertObject(val, method);
            method.invoke(dataRow, val);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public Object convertObject(Object val, Method method) {
        if (val == null) return val;

        Class paramClass = method.getParameters()[0].getType();
        //#TODO: add to support all Date Objects(LocalDate,LocalDateTime
        if (val instanceof Date) {

            if (paramClass == LocalDate.class)
                return Instant.ofEpochMilli(((Date) val).getTime()).atZone(Clock.systemDefaultZone().getZone()).toLocalDate();

            if (paramClass == LocalDateTime.class)
                return Instant.ofEpochMilli(((Date) val).getTime()).atZone(Clock.systemDefaultZone().getZone()).toLocalDateTime();
        }

        if (Number.class.isAssignableFrom(paramClass) && val instanceof String) {
            try {
                return NumberUtils.parseNumber((String) val, paramClass);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (val instanceof BigDecimal) {

            return NumberUtils.convertNumberToTargetClass((BigDecimal) val, paramClass);
        }

        return val;
    }

    public Object getValue(T dataRow, String fieldName) {

        Method method = BeanUtils.findMethodWithMinimalParameters(excelProcessor.getBeanClass(), "get" + fieldName);
        try {
            Object val = method.invoke(dataRow);
            if (val instanceof Temporal) {
                val = getDateValue(val);
            }
            return val;
        } catch (Exception e) {
            throw new ExcelProcessorException("Error while getting value for field: " + fieldName);
        }
    }

    public Object getDateValue(Object val) {

        if (val instanceof Temporal) {
            return DateConvertUtils.asUtilDate(val);
        }
        return val;
    }
}