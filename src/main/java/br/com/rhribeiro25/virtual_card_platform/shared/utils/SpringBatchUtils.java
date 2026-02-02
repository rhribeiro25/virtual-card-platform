package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

public class SpringBatchUtils {

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
