package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {

    public String normalize(String key) {
        return key == null ? null : key.trim().toUpperCase();
    }
}
