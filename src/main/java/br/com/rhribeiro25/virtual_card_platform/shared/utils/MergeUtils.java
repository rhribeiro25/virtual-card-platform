package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class MergeUtils {

    public static <T> void mergeField(T currentValue, T newValue, Consumer<T> setter) {
        if (newValue == null) return;

        boolean shouldUpdate = switch (newValue) {
            case BigDecimal bd -> currentValue == null || bd.compareTo((BigDecimal) currentValue) != 0;
            case String s -> !s.isBlank() && !s.equals(currentValue);
            default -> !java.util.Objects.equals(currentValue, newValue);
        };

        if (shouldUpdate) {
            setter.accept(newValue);
        }
    }

}
