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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.annotation.IgnoreCloak;
import com.flipkart.masquerade.rule.*;
import com.flipkart.masquerade.serialization.SerializationProperty;
import com.flipkart.masquerade.util.FieldDescriptor;
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

        List<Field> nonStaticFields = getNonStaticFields(clazz).stream().filter(field -> !field.isAnnotationPresent(IgnoreCloak.class) && !field.isAnnotationPresent(JsonIgnore.class)).collect(Collectors.toList());
        nonStaticFields = orderedFields(nonStaticFields, clazz.getAnnotation(JsonPropertyOrder.class));
        int actualSize = nonStaticFields.size();
        int processed = 0;
        /* Only consider fields for processing that are not static */
        for (Field field : nonStaticFields) {
            if (field.getType().isPrimitive()) {
                if (!configuration.isNativeSerializationEnabled()) {
                    continue;
                }
            }

            if (configuration.isNativeSerializationEnabled()) {
                methodBuilder.addStatement("$L.append($S).append($S).append($S).append($S)", SERIALIZED_OBJECT, QUOTES, field.getName(), QUOTES, ":");
            }

            Class<? extends Annotation> annotationClass = rule.getAnnotationClass();
            Annotation[] annotations = field.getAnnotationsByType(annotationClass);
            if (annotations != null && annotations.length != 0) {
                for (Annotation annotation : annotations) {
                    constructOperation(rule, annotationClass, annotation, methodBuilder, field.getName(), clazz);
                }
            }

            addRecursiveStatement(rule, clazz, field, methodBuilder, initializer);
            if (++processed != actualSize && configuration.isNativeSerializationEnabled()) {
                methodBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, ",");
            }
        }

        if (configuration.isNativeSerializationEnabled()) {
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

    private List<Field> orderedFields(List<Field> fields, JsonPropertyOrder propertyOrder) {
        if (!configuration.isNativeSerializationEnabled()) {
            return fields;
        }

        List<Field> sortedFields = new ArrayList<>();
        if (propertyOrder != null && propertyOrder.value().length > 0) {
            for (String name : propertyOrder.value()) {
                int index = findField(name, fields);
                if (index >= 0) {
                    sortedFields.add(fields.remove(index));
                }
            }
        }

        boolean sortedAlphabetically = configuration.serializationProperties().contains(SerializationProperty.SORT_PROPERTIES_ALPHABETICALLY);
        if (!sortedAlphabetically) {
            sortedFields.addAll(fields);
            return sortedFields;
        }

        fields.sort(Comparator.comparing(Field::getName));
        sortedFields.addAll(fields);
        return sortedFields;
    }

    private int findField(String name, List<Field> fields) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
