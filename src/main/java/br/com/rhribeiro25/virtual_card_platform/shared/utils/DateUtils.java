package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtils {

    public LocalDateTime MM_YY_TO_LocalDateTime(String expiryTxt) {
        if (expiryTxt == null || expiryTxt.isBlank()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth yearMonth = YearMonth.parse(expiryTxt.trim(), formatter);

        return yearMonth
                .atEndOfMonth()
                .atTime(12, 0, 0);
    }
}
