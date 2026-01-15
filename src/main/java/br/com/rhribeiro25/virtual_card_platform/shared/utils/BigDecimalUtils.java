package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.StringUtils.normalize;

@Component
public class BigDecimalUtils {

    public BigDecimal stringToBigDecimal(String decimalString){
        return new BigDecimal(normalize(decimalString).replace(",", "."));
    }
}
