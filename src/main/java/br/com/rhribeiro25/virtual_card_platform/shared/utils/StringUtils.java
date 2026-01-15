package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.springframework.util.StringUtils.hasText;

@Component
public class StringUtils {

    public static String normalize(String key) {
        return key == null ? null : key.trim().toUpperCase();
    }

    public static void updateIfHasText(String existing, String incoming, Consumer<String> setter) {
        if (hasText(incoming) && !Objects.equals(existing, incoming)) {
            setter.accept(incoming);
        }
    }

    /**
     * Executes the mapper only if the given String has text.
     *
     * @param value  source string
     * @param mapper mapping logic
     * @param <T>    mapped type
     * @return mapped value or null
     */
    public static <T> T whenHasText(String value, Supplier<T> mapper) {
        return hasText(value) ? mapper.get() : null;
    }

    /**
     * Updates a field using the provided setter only if the new value is not null
     * and is different from the existing value.
     *
     * @param existingValue current field value
     * @param newValue      new value
     * @param setter        setter reference
     * @param <T>           field type
     */
    public static <T> void updateIfChanged(
            T existingValue,
            T newValue,
            Consumer<T> setter
    ) {
        if (newValue != null && !Objects.equals(existingValue, newValue)) {
            setter.accept(newValue);
        }
    }
}

