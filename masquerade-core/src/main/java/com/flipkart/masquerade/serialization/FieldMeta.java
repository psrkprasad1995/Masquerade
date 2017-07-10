package com.flipkart.masquerade.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

import static com.flipkart.masquerade.util.Helper.getGetterName;
import static com.flipkart.masquerade.util.Helper.getSetterName;

/**
 * Created by shrey.garg on 10/07/17.
 */
public class FieldMeta {
    private final String name;
    private final Class<?> type;
    private String serializableName;
    private final Field field;

    public FieldMeta(Field field, Class<?> clazz) {
        this.name = field.getName();
        this.type = field.getType();
        this.serializableName = getSerializableName(field, clazz);
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public String getSerializableName() {
        return serializableName;
    }

    private String getSerializableName(Field field, Class<?> clazz) {
        String getter = getGetterName(field.getName(), field.getType().equals(Boolean.TYPE), field.getType().isPrimitive());
        String setter = getSetterName(field.getName());
        Method getterMethod;
        Method setterMethod;
        try {
            getterMethod = clazz.getMethod(getter);
            setterMethod = clazz.getMethod(setter, field.getType());
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("A cloak-able class should have a getter and setter defined for all fields. Class: " + clazz.getName() + " Field: " + field.getName());
        }

        JsonProperty getterJsonProperty = getterMethod.getAnnotation(JsonProperty.class);
        JsonProperty setterJsonProperty = setterMethod.getAnnotation(JsonProperty.class);
        JsonProperty fieldJsonProperty = field.getAnnotation(JsonProperty.class);
        return Optional.ofNullable(fieldJsonProperty).map(valueFunc)
                .orElse(Optional.ofNullable(getterJsonProperty).map(valueFunc)
                        .orElse(Optional.ofNullable(setterJsonProperty).map(valueFunc)
                                .orElse(field.getName())));
    }

    public Field getField() {
        return field;
    }

    private Function<JsonProperty, String> valueFunc = p -> {
        if (p.value().trim().isEmpty()) {
            return null;
        }
        return p.value();
    };
}
