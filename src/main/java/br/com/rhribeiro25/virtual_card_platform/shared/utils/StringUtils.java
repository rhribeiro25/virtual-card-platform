package br.com.rhribeiro25.virtual_card_platform.shared.utils;

public class StringUtils {

    public static String normalizeKey(String key) {
        return key == null ? null : key.trim().toUpperCase();
    }
}
