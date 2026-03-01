package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class SpringBatchUtils {

    public static Map<String, Card> cardMap;

    public static String getComponentName(Class<?> clazz) {
        return clazz.getAnnotation(Component.class).value();
    }

    public static String getConfigurationName(Class<?> clazz) {
        return clazz.getAnnotation(Configuration.class).value();
    }

    public static String getClassName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
}
