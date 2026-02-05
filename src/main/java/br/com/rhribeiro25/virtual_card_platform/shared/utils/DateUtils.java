package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static LocalDateTime MM_YY_ToLocalDateTime(String expiryTxt) {
        if (!StringUtils.hasText(expiryTxt)) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth yearMonth = YearMonth.parse(expiryTxt.trim(), formatter);
        return yearMonth.atEndOfMonth().atTime(12, 0, 0);
    }

    public static LocalDateTime YYYYMMDD_ToLocalDateTime(String expiryTxt) {
        if (!StringUtils.hasText(expiryTxt)) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        YearMonth yearMonth = YearMonth.parse(expiryTxt.trim(), formatter);
        return yearMonth.atEndOfMonth().atTime(12, 0, 0);
    }

    public static LocalDate YYYY_MM_DD_ToLocalDate(String expiryTxt) {
        if (!StringUtils.hasText(expiryTxt)) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(expiryTxt.trim(), formatter);
    }
}
