/*
 * Copyright 2017 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.masquerade.processor;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.annotation.IgnoreCloak;
import com.flipkart.masquerade.rule.*;
import com.flipkart.masquerade.serialization.FieldMeta;
import com.flipkart.masquerade.serialization.SerializationProperty;
import com.flipkart.masquerade.util.FieldDescriptor;
import com.flipkart.masquerade.util.Helper;
import com.google.common.base.Defaults;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.flipkart.masquerade.util.Helper.*;
import static com.flipkart.masquerade.util.Strings.*;

/**
 * Processor that creates an implementation class for a Mask interface
 * <p />
 * Created by shrey.garg on 12/05/17.
 */
public class OverrideProcessor extends BaseOverrideProcessor {
    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder  Entry class under construction for the cycle
     */
    public OverrideProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        super(configuration, cloakBuilder);
    }

    /**
     * @param rule The rule which is being processed
     * @param clazz The Class for which the Rule Mask interface will be implemented
     * @param initializer The Initialization block in which the map entry for the implementation will be added
     * @return A fully constructed TypeSpec object for the implementation
     */
    public Optional<TypeSpec> createOverride(Rule rule, Class<?> clazz, CodeBlock.Builder initializer) {
        MethodSpec.Builder methodBuilder = generateOverrideMethod(rule, clazz);

        if (configuration.isNativeSerializationEnabled()) {
            methodBuilder.addStatement("$T $L = new $T($S)", StringBuilder.class, SERIALIZED_OBJECT, StringBuilder.class, "{");
        }

        /* Only consider fields for processing that are not static and remove fields that are to be ignored */
        List<Field> originalFields = getNonStaticFields(clazz).stream().filter(field -> !field.isAnnotationPresent(IgnoreCloak.class) && !field.isAnnotationPresent(JsonIgnore.class)).collect(Collectors.toList());
        List<FieldMeta> nonStaticFields = orderedFields(originalFields, clazz);
        addSubTypeControl(clazz, nonStaticFields);
        for (FieldMeta field : nonStaticFields) {
            if (field.getType().isPrimitive()) {
                if (!configuration.isNativeSerializationEnabled()) {
                    continue;
                }
            }

            if (configuration.isNativeSerializationEnabled()) {
                if (field.isSynthetic()) {
                    methodBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, QUOTES + field.getSerializableName() + QUOTES + ":" + QUOTES + field.getSyntheticValue() + QUOTES);
                    methodBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, ",");
                    continue;
                }
            }

            if (!field.getType().isPrimitive()) {
                Class<? extends Annotation> annotationClass = rule.getAnnotationClass();
                Annotation[] annotations = field.getField().getAnnotationsByType(annotationClass);
                if (annotations != null && annotations.length != 0) {
                    for (Annotation annotation : annotations) {
                        constructOperation(rule, annotationClass, annotation, methodBuilder, field.getName(), clazz);
                    }
                }
            }

            if (configuration.isNativeSerializationEnabled()) {
                resolveInclusionLevel(clazz, field);
                if (field.getInclusionLevel() != JsonInclude.Include.ALWAYS) {
                    CodeBlock inclusionCondition = constructInclusionCondition(field);
                    if (field.isMaskable()) {
                        methodBuilder.beginControlFlow("$L", inclusionCondition);
                    }
                }
                methodBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, QUOTES + field.getSerializableName() + QUOTES + ":");
            }

            addRecursiveStatement(rule, clazz, field.getField(), methodBuilder, initializer);

            if (configuration.isNativeSerializationEnabled()) {
                methodBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, ",");
                if (field.getInclusionLevel() != JsonInclude.Include.ALWAYS && field.isMaskable()) {
                    methodBuilder.endControlFlow();
                }
            }
        }

        if (configuration.isNativeSerializationEnabled()) {
            methodBuilder.beginControlFlow("if ($L.length() > 1)", SERIALIZED_OBJECT);
            methodBuilder.addStatement("$L.deleteCharAt($L.length() - 1)", SERIALIZED_OBJECT, SERIALIZED_OBJECT);
            methodBuilder.endControlFlow();

            methodBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, "}");
            methodBuilder.addStatement("return $L.toString()", SERIALIZED_OBJECT);
        }

        MethodSpec methodSpec = methodBuilder.build();
        if (methodSpec.code.isEmpty()) {
            addNoOpInitializerCode(rule, clazz, initializer);
            return Optional.empty();
        }

        String implName = getImplementationName(rule, clazz);
        addInitializerCode(rule, clazz, initializer, implName);
        return Optional.ofNullable(generateImplementationType(rule, clazz, implName, methodSpec));
    }

    /**
     * Adds a call to the Entry class if the current field needs to be processed as well
     * @param field Current Field
     * @param methodBuilder Current method builder
     */
    private void addRecursiveStatement(Rule rule, Class<?> clazz, Field field, MethodSpec.Builder methodBuilder, CodeBlock.Builder initializer) {
        /* Does not add the statement if the field is primitive, primitive wrapper, String or an Enum */
        if ((!field.getType().isPrimitive() &&
                !getWrapperTypes().contains(field.getType()) &&
                !String.class.isAssignableFrom(field.getType()) &&
                !field.getType().isEnum()) || configuration.isNativeSerializationEnabled()) {
            String getter = getGetterName(field.getName(), field.getType().equals(Boolean.TYPE), field.getType().isPrimitive());
            try {
                clazz.getMethod(getter);
            } catch (NoSuchMethodException e) {
                throw new UnsupportedOperationException("A cloak-able class should have a getter defined for all fields. Class: " + clazz.getName() + " Field: " + field.getName());
            }

            if (field.getType().isEnum()) {
                addEnumInitializerCode(rule, field.getType(), initializer);
            }

            if (configuration.isNativeSerializationEnabled()) {
                methodBuilder.addStatement("$L.append($L.$L($L.$L(), $L))", SERIALIZED_OBJECT, CLOAK_PARAMETER, ENTRY_METHOD, OBJECT_PARAMETER, getter, EVAL_PARAMETER);
            } else {
                methodBuilder.addStatement("$L.$L($L.$L(), $L)", CLOAK_PARAMETER, ENTRY_METHOD, OBJECT_PARAMETER, getter, EVAL_PARAMETER);
            }
        }
    }

    private void addNoOpInitializerCode(Rule rule, Class<?> clazz, CodeBlock.Builder initializer) {
        initializer.addStatement("$L.put($S, $L)", rule.getName(), clazz.getName(), getNoOpVariableName(rule));
    }

    private void addEnumInitializerCode(Rule rule, Class<?> clazz, CodeBlock.Builder initializer) {
        initializer.addStatement("$L.put($S, $L)", rule.getName(), clazz.getName(), getEnumVariableName(rule));
    }

    private void addInitializerCode(Rule rule, Class<?> clazz, CodeBlock.Builder initializer, String implName) {
        ClassName cloakName = ClassName.get(getImplementationPackage(configuration, clazz), implName);
        initializer.addStatement("$L.put($S, new $T())", rule.getName(), clazz.getName(), cloakName);
    }

    private void constructOperation(Rule rule, Class<? extends Annotation> annotationClass, Annotation annotation, MethodSpec.Builder builder, String fieldName, Class<?> clazz) {
        List<Object> operands = new ArrayList<>();

        CompositeRule baseRule = rule.getValueRule();
        String operation = constructBasicOperation(baseRule, baseRule.getConjunction(), annotationClass, annotation, operands);

        String setter = getSetterName(fieldName);
        Arrays.stream(clazz.getMethods())
                .filter(method -> method.getName().equals(setter))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("A cloak-able class should have a setter defined for all fields. Class: " + clazz.getName() + " Field: " + fieldName));

        builder.beginControlFlow("if (" + operation + ")", operands.toArray());
        builder.addStatement("$L.$L(null)", OBJECT_PARAMETER, setter);
        builder.endControlFlow();
    }

    private String constructBasicOperation(CompositeRule compositeRule, Conjunction conjunction, Class<? extends Annotation> annotationClass, Annotation annotation, List<Object> operands) {
        StringBuilder operation = new StringBuilder();
        for (ValueRule valueRule : compositeRule.getValueRules()) {
            if (valueRule instanceof CompositeRule) {
                CompositeRule innerRule = (CompositeRule) valueRule;
                String constructedCompositeOperation = constructBasicOperation(innerRule, innerRule.getConjunction(), annotationClass, annotation, operands);
                if (constructedCompositeOperation.length() == 0) {
                    continue;
                }

                operation.append("(").append(constructedCompositeOperation).append(")").append(" ").append(conjunction.getSymbol()).append(" ");
                continue;
            }

            Object value;
            Object defaultValue;

            BasicRule basicRule = (BasicRule) valueRule;

            try {
                Method annotationValue = annotationClass.getDeclaredMethod(basicRule.getAnnotationMember());
                value = annotationValue.invoke(annotation);
                defaultValue = annotationValue.getDefaultValue();
            } catch (Exception e) {
                throw new UnsupportedOperationException("Please provide a annotation member that exists");
            }

            if (basicRule.isDefaultIgnored() && value.equals(defaultValue)) {
                continue;
            }

            FieldDescriptor descriptor = generateDescriptor(value);
            basicRule.getOperator().getGenerateOperation().accept(operation, descriptor);
            operation.append(" ").append(conjunction.getSymbol()).append(" ");

            String evalFunc = getEvaluationFunction(basicRule);
            value = handleEnum(descriptor, value);

            operands.add(evalFunc);
            operands.add(value);
        }

        return operation.length() < 4 ? operation.toString() : operation.delete(operation.length() - 4, operation.length()).toString();
    }

    private FieldDescriptor generateDescriptor(Object value) {
        boolean isPrimitive = value.getClass().isPrimitive() || getWrapperTypes().contains(value.getClass());
        return new FieldDescriptor(
                isPrimitive,
                isPrimitive || value.getClass().isEnum(),
                isPrimitive || Comparable.class.isAssignableFrom(value.getClass()),
                value.getClass().isEnum());
    }

    private Object handleEnum(FieldDescriptor descriptor, Object value) {
        if (!descriptor.isEnumeration()) {
            return value;
        }

        ClassName enumName = ClassName.get(value.getClass().getPackage().getName(), value.getClass().getSimpleName());
        return CodeBlock.of("$T.$L", enumName, value);
    }

    private List<FieldMeta> orderedFields(List<Field> fields, Class<?> clazz) {
        List<FieldMeta> fieldMetas = transform(fields, clazz);
        if (!configuration.isNativeSerializationEnabled()) {
            return fieldMetas;
        }

        List<FieldMeta> sortedFields = new ArrayList<>();
        JsonPropertyOrder propertyOrder = getAnnotation(clazz, JsonPropertyOrder.class);
        if (propertyOrder != null && propertyOrder.value().length > 0) {
            for (String name : propertyOrder.value()) {
                int index = findField(name, fieldMetas);
                if (index >= 0) {
                    sortedFields.add(fieldMetas.remove(index));
                }
            }
        }

        boolean sortedAlphabetically = configuration.serializationProperties().contains(SerializationProperty.SORT_PROPERTIES_ALPHABETICALLY);
        if (!sortedAlphabetically) {
            sortedFields.addAll(fieldMetas);
            return sortedFields;
        }

        fieldMetas.sort(Comparator.comparing(FieldMeta::getSerializableName));
        sortedFields.addAll(fieldMetas);
        return sortedFields;
    }

    private List<FieldMeta> transform(List<Field> fields, Class<?> clazz) {
        return fields.stream().map(field -> new FieldMeta(field, clazz)).collect(Collectors.toList());
    }

    private int findField(String name, List<FieldMeta> fields) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void addSubTypeControl(Class<?> clazz, List<FieldMeta> fields) {
        if (!configuration.isNativeSerializationEnabled()) {
            return;
        }

        JsonTypeInfo jsonTypeInfo = getAnnotation(clazz, JsonTypeInfo.class);
        JsonSubTypes jsonSubTypes = getAnnotation(clazz, JsonSubTypes.class);
        if (jsonTypeInfo == null || jsonSubTypes == null) {
            return;
        }

        if (!jsonTypeInfo.include().equals(JsonTypeInfo.As.PROPERTY)) {
            return;
        }

        Optional<JsonSubTypes.Type> subType = Arrays.stream(jsonSubTypes.value()).filter(t -> t.value().equals(clazz)).findFirst();
        if (!subType.isPresent()) {
            return;
        }

        fields.add(0, new FieldMeta(jsonTypeInfo.property(), clazz, subType.get().name()));
    }

    private void resolveInclusionLevel(Class<?> clazz, FieldMeta fieldMeta) {
        JsonSerialize classJsonSerialize = getAnnotation(clazz, JsonSerialize.class);
        JsonInclude classJsonInclude = getAnnotation(clazz, JsonInclude.class);

        JsonInclude.Include classInclusion = null;
        if (classJsonInclude == null && classJsonSerialize != null) {
            classInclusion = mapJsonSerialize(classJsonSerialize.include());
        } else if (classJsonInclude != null) {
            classInclusion = classJsonInclude.value();
        }

        JsonSerialize fieldJsonSerialize = fieldMeta.getField().getAnnotation(JsonSerialize.class);
        JsonInclude fieldJsonInclude = fieldMeta.getField().getAnnotation(JsonInclude.class);

        JsonInclude.Include fieldInclusion = null;
        if (fieldJsonInclude == null && fieldJsonSerialize != null) {
            fieldInclusion = mapJsonSerialize(fieldJsonSerialize.include());
        } else if (fieldJsonInclude != null) {
            fieldInclusion = fieldJsonInclude.value();
        }

        fieldMeta.setInclusionLevel(Optional.ofNullable(fieldInclusion).orElse(Optional.ofNullable(classInclusion).orElse(JsonInclude.Include.ALWAYS)));
    }

    private JsonInclude.Include mapJsonSerialize(JsonSerialize.Inclusion inclusion) {
        switch (inclusion) {
            case NON_NULL:
                return JsonInclude.Include.NON_NULL;
            case NON_EMPTY:
                return JsonInclude.Include.NON_EMPTY;
            case NON_DEFAULT:
                return JsonInclude.Include.NON_DEFAULT;
            case DEFAULT_INCLUSION:
            case ALWAYS:
            default:
                return JsonInclude.Include.ALWAYS;
        }
    }

    private CodeBlock constructInclusionCondition(FieldMeta fieldMeta) {
        Field field = fieldMeta.getField();
        String getterName = getGetterName(field.getName(), field.getType().equals(Boolean.TYPE), field.getType().isPrimitive());
        if (field.getType().isPrimitive()) {
            if (fieldMeta.getInclusionLevel() == JsonInclude.Include.NON_DEFAULT) {
                Object value = Defaults.defaultValue(field.getType());
                return CodeBlock.of("if ($L.$L() != $L)", OBJECT_PARAMETER, getterName, value);
            }
            fieldMeta.setMaskable(false);
            return null;
        } else {
            switch (fieldMeta.getInclusionLevel()) {
                case NON_DEFAULT:
                case NON_NULL:
                    return CodeBlock.of("if ($L.$L() != null)", OBJECT_PARAMETER, getterName);
                case NON_EMPTY:
                    if (getEmptiableTypes().stream().noneMatch(t -> t.isAssignableFrom(field.getType()))) {
                        fieldMeta.setMaskable(false);
                        return null;
                    }
                    return CodeBlock.of("if ($L.$L() != null && !$L.$L().isEmpty())", OBJECT_PARAMETER, getterName, OBJECT_PARAMETER, getterName);
                default:
                    fieldMeta.setMaskable(false);
                    return null;
            }
        }
    }
}
