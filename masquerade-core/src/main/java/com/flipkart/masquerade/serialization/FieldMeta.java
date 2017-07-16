package com.flipkart.masquerade.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

import static com.flipkart.masquerade.util.Helper.*;

/**
 * Created by shrey.garg on 10/07/17.
 */
public class FieldMeta {
    private final String name;
    private final Class<?> type;
    private final String serializableName;
    private final Field field;
    private final boolean synthetic;
    private final String syntheticValue;
    private JsonInclude.Include inclusionLevel;
    private boolean maskable = true;

    public FieldMeta(Field field, Class<?> clazz) {
        this.name = field.getName();
        this.type = field.getType();
        this.serializableName = getSerializableName(field, clazz);
        this.field = field;
        this.synthetic = false;
        this.syntheticValue = null;
    }

    public FieldMeta(String name, Class<?> type, String syntheticValue) {
        this.name = name;
        this.serializableName = name;
        this.type = type;
        this.field = null;
        this.synthetic = true;
        this.syntheticValue = syntheticValue;
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

    public boolean isSynthetic() {
        return synthetic;
    }

    public String getSyntheticValue() {
        return syntheticValue;
    }

    private String getSerializableName(Field field, Class<?> clazz) {
        String getter = getGetterName(field.getName(), isBoolean(field.getType()), field.getType().isPrimitive());
        String setter = getSetterName(field.getName(), isBoolean(field.getType()));
        Method getterMethod;
        Method setterMethod = null;
        try {
            getterMethod = clazz.getMethod(getter);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("A cloak-able class should have a getter defined for all fields. Class: " + clazz.getName() + " Field: " + field.getName());
        }

        try {
            setterMethod = clazz.getMethod(setter, field.getType());
        } catch (NoSuchMethodException ignored) {

        }

        JsonProperty getterJsonProperty = getterMethod.getAnnotation(JsonProperty.class);
        JsonProperty setterJsonProperty = Optional.ofNullable(setterMethod).map(m -> m.getAnnotation(JsonProperty.class)).orElse(null);
        JsonProperty fieldJsonProperty = field.getAnnotation(JsonProperty.class);
        return Optional.ofNullable(fieldJsonProperty).map(valueFunc)
                .orElse(Optional.ofNullable(getterJsonProperty).map(valueFunc)
                        .orElse(Optional.ofNullable(setterJsonProperty).map(valueFunc)
                                .orElse(handleBooleans(field))));
    }

    public Field getField() {
        return field;
    }

    private final Function<JsonProperty, String> valueFunc = p -> {
        if (p.value().trim().isEmpty()) {
            return null;
        }
        return p.value();
    };

    private String handleBooleans(Field field) {
        if (!isBoolean(field.getType())) {
            return field.getName();
        }

        return deCapitalize(handleIsPrefix(field.getName()));
    }

    public JsonInclude.Include getInclusionLevel() {
        return inclusionLevel;
    }

    public void setInclusionLevel(JsonInclude.Include inclusionLevel) {
        this.inclusionLevel = inclusionLevel;
    }

    public boolean isMaskable() {
        return maskable;
    }

    public void setMaskable(boolean maskable) {
        this.maskable = maskable;
    }
}
