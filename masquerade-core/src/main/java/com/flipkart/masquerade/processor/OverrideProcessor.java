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

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.annotation.IgnoreCloak;
import com.flipkart.masquerade.rule.*;
import com.flipkart.masquerade.util.FieldDescriptor;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.flipkart.masquerade.util.Helper.*;
import static com.flipkart.masquerade.util.Strings.*;

/**
 * Processor that creates an implementation class for a Mask interface
 * <p />
 * Created by shrey.garg on 12/05/17.
 */
public class OverrideProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public OverrideProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    /**
     * @param rule The rule which is being processed
     * @param clazz The Class for which the Rule Mask interface will be implemented
     * @param initializer The Initialization block in which the map entry for the implementation will be added
     * @return A fully constructed TypeSpec object for the implementation
     */
    public TypeSpec createOverride(Rule rule, Class<?> clazz, CodeBlock.Builder initializer) {
        String implName = getImplementationName(rule, clazz);

        addInitializerCode(rule, clazz, initializer, implName);

        MethodSpec.Builder methodBuilder = generateOverrideMethod(rule, clazz);

        /* Only consider fields for processing that are not static */
        for (Field field : getNonStaticFields(clazz)) {
            if (field.getType().isPrimitive() || field.isAnnotationPresent(IgnoreCloak.class)) {
                continue;
            }

            Class<? extends Annotation> annotationClass = rule.getAnnotationClass();
            Annotation[] annotations = field.getAnnotationsByType(annotationClass);
            if (annotations != null && annotations.length != 0) {
                for (Annotation annotation : annotations) {
                    constructOperation(rule, annotationClass, annotation, methodBuilder, field.getName(), clazz);
                }
            }

            addRecursiveStatement(clazz, field, methodBuilder);
        }

        return generateImplementationType(rule, clazz, implName, methodBuilder.build());
    }

    /**
     * @param rule Current Rule
     * @param clazz Current Class
     * @return A MethodSpec builder which overrides the interface method
     */
    private MethodSpec.Builder generateOverrideMethod(Rule rule, Class<?> clazz) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(INTERFACE_METHOD);
        methodBuilder.addAnnotation(Override.class);
        methodBuilder.addModifiers(Modifier.PUBLIC);
        methodBuilder.addParameter(clazz, OBJECT_PARAMETER);
        methodBuilder.addParameter(rule.getEvaluatorClass(), EVAL_PARAMETER);
        methodBuilder.addParameter(getEntryClass(configuration), CLOAK_PARAMETER);
        return methodBuilder;
    }

    /**
     * @param rule Current Rule
     * @param clazz Current Class
     * @param implName Name of the implementation class
     * @param method The overridden method the implementation class
     * @return Constructed TypeSpec for the implementation class
     */
    private TypeSpec generateImplementationType(Rule rule, Class<?> clazz, String implName, MethodSpec method) {
        TypeSpec.Builder implBuilder = TypeSpec.classBuilder(implName);
        implBuilder.addModifiers(Modifier.PUBLIC);
        /* Implements the interface and attaches the current class as a Generic bound */
        implBuilder.addSuperinterface(ParameterizedTypeName.get(getRuleInterface(configuration, rule), TypeName.get(clazz)));
        implBuilder.addMethod(method);
        return implBuilder.build();
    }

    /**
     * Adds a call to the Entry class if the current field needs to be processed as well
     * @param field Current Field
     * @param methodBuilder Current method builder
     */
    private void addRecursiveStatement(Class<?> clazz, Field field, MethodSpec.Builder methodBuilder) {
        /* Does not add the statement if the field is primitive, primitive wrapper, String or an Enum */
        if (!field.getType().isPrimitive() &&
                !getWrapperTypes().contains(field.getType()) &&
                !String.class.isAssignableFrom(field.getType()) &&
                !field.getType().isEnum()) {
            String getter = getGetterName(field.getName());
            try {
                clazz.getMethod(getter);
            } catch (NoSuchMethodException e) {
                throw new UnsupportedOperationException("A cloak-able class should have a getter defined for all fields. Class: " + clazz.getName() + " Field: " + field.getName());
            }
            methodBuilder.addStatement("$L.$L($L.$L(), $L)", CLOAK_PARAMETER, ENTRY_METHOD, OBJECT_PARAMETER, getter, EVAL_PARAMETER);
        }
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
}
