package com.flipkart.masquerade.processor;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.rule.Rule;
import com.flipkart.masquerade.rule.ValueRule;
import com.flipkart.masquerade.util.FieldDescriptor;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
            Class<? extends Annotation> annotationClass = rule.getAnnotationClass();
            Annotation[] annotations = field.getAnnotationsByType(annotationClass);
            if (annotations != null && annotations.length != 0) {
                for (Annotation annotation : annotations) {
                    constructOperation(rule, annotationClass, annotation, methodBuilder, field.getName());
                }
            }

            addRecursiveStatement(field, methodBuilder);
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
    private void addRecursiveStatement(Field field, MethodSpec.Builder methodBuilder) {
        /* Does not add the statement if the field is primitive, primitive wrapper, String or an Enum */
        if (!field.getType().isPrimitive() &&
                !getWrapperTypes().contains(field.getType()) &&
                !String.class.isAssignableFrom(field.getType()) &&
                !field.getType().isEnum()) {
            methodBuilder.addStatement("$L.$L($L.$L(), $L)", CLOAK_PARAMETER, ENTRY_METHOD, OBJECT_PARAMETER, getGetterName(field.getName()), EVAL_PARAMETER);
        }
    }

    private void addInitializerCode(Rule rule, Class<?> clazz, CodeBlock.Builder initializer, String implName) {
        ClassName cloakName = ClassName.get(getImplementationPackage(configuration, clazz), implName);
        initializer.addStatement("$L.put($S, new $T())", rule.getName(), clazz.getName(), cloakName);
    }

    private void constructOperation(Rule rule, Class<? extends Annotation> annotationClass, Annotation annotation, MethodSpec.Builder builder, String fieldName) {
        List<Object> operands = new ArrayList<>();
        StringBuilder operation = new StringBuilder();

        for (ValueRule valueRule : rule.getValueRules()) {
            Object value;
            Object defaultValue;
            try {
                Method annotationValue = annotationClass.getDeclaredMethod(valueRule.getAnnotationMember());
                value = annotationValue.invoke(annotation);
                defaultValue = annotationValue.getDefaultValue();
            } catch (Exception e) {
                throw new UnsupportedOperationException("Please provide a annotation member that exists");
            }

            if (valueRule.isDefaultIgnored() && value.equals(defaultValue)) {
                continue;
            }

            FieldDescriptor descriptor = generateDescriptor(value);
            valueRule.getOperator().getGenerateOperation().accept(operation, descriptor);
            operation.append(" && ");

            String evalFunc = getEvaluationFunction(valueRule);
            value = handleEnum(descriptor, value);

            operands.add(evalFunc);
            operands.add(value);
        }

        operation.delete(operation.length() - 4, operation.length());
        builder.beginControlFlow("if (" + operation.toString() + ")", operands.toArray());
        builder.addStatement("$L.$L(null)", OBJECT_PARAMETER, getSetterName(fieldName));
        builder.endControlFlow();
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
