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
import com.flipkart.masquerade.rule.Rule;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.Map;

import static com.flipkart.masquerade.util.Helper.getRuleInterface;
import static com.flipkart.masquerade.util.Strings.*;

/**
 * Processor which adds the entry point method in the Entry class
 * <p />
 * Created by shrey.garg on 12/05/17.
 */
public class RuleObjectProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public RuleObjectProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    /**
     * @param rule Rule for which the entry point will be added
     */
    public void addEntry(Rule rule) {
        MethodSpec.Builder objectMaskBuilder = MethodSpec.methodBuilder(ENTRY_METHOD);
        objectMaskBuilder.addModifiers(Modifier.PUBLIC);
        /* Suppress warnings as the generated code will never cause a CLassCastException */
        objectMaskBuilder.addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build());
        /* Adds an Object class parameter for which all the process at runtime will happen */
        objectMaskBuilder.addParameter(Object.class, OBJECT_PARAMETER);
        /* The second parameter refers to the Evaluator Object which will be used for comparisons */
        objectMaskBuilder.addParameter(rule.getEvaluatorClass(), EVAL_PARAMETER);

        /* If a null Object is passed, return immediately */
        objectMaskBuilder.beginControlFlow("if ($L == null)", OBJECT_PARAMETER);
        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.addStatement("return null");
        } else {
            objectMaskBuilder.addStatement("return");
        }
        objectMaskBuilder.endControlFlow();

        /* Fetch the Mask implementation object from Map and assign it to an interface reference for the Rule */
        objectMaskBuilder.addStatement("$T $L = this.$L.get($L.getClass().getName())",
                getRuleInterface(configuration, rule),
                MASKER_VARIABLE,
                rule.getName(),
                OBJECT_PARAMETER);

        /* Check if the retrieved Object is present */
        objectMaskBuilder.beginControlFlow("if ($L != null)", MASKER_VARIABLE);
        /* If it is, then call the mask method for the Object */
        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.addStatement("return $L.$L($L, $L, this)", MASKER_VARIABLE, INTERFACE_METHOD, OBJECT_PARAMETER, EVAL_PARAMETER);
        } else {
            objectMaskBuilder.addStatement("$L.$L($L, $L, this)", MASKER_VARIABLE, INTERFACE_METHOD, OBJECT_PARAMETER, EVAL_PARAMETER);
        }

        objectMaskBuilder.nextControlFlow("else");
        /* Otherwise, check if the Object is an instance of Map */
        objectMaskBuilder.beginControlFlow("if ($L instanceof $T)", OBJECT_PARAMETER, Map.class);
        /* If it is, then recursively call this entry method with the List of map values */
        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.addStatement("$T $L = new $T($S)", StringBuilder.class, SERIALIZED_OBJECT, StringBuilder.class, "{");

            objectMaskBuilder.addStatement(
                    "(($T) $L).forEach((k, v) -> $L.append($S).append(k).append($S).append($S).append(this.$L(v, $L)).append($S))",
                    Map.class, OBJECT_PARAMETER, SERIALIZED_OBJECT, QUOTES, QUOTES, ":", ENTRY_METHOD, EVAL_PARAMETER, ",");

            objectMaskBuilder.beginControlFlow("if ($L.length() > 1)", SERIALIZED_OBJECT);
            objectMaskBuilder.addStatement("$L.deleteCharAt($L.length() - 1)", SERIALIZED_OBJECT, SERIALIZED_OBJECT);
            objectMaskBuilder.endControlFlow();
            objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, "}");
            objectMaskBuilder.addStatement("return $L.toString()", SERIALIZED_OBJECT);
        } else {
            objectMaskBuilder.addStatement("this.$L(((Map) $L).values(), $L)", ENTRY_METHOD, OBJECT_PARAMETER, EVAL_PARAMETER);
        }

        /* If it's not a Map, then check if the Object is a collection */
        objectMaskBuilder.nextControlFlow("else if ($L instanceof $T)", OBJECT_PARAMETER, Collection.class);
        /* If it is, then iterate over the collection */
        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.addStatement("$T $L = new $T($S)", StringBuilder.class, SERIALIZED_OBJECT, StringBuilder.class, "[");
        }
        objectMaskBuilder.beginControlFlow("for (Object o : ((Collection) $L))", OBJECT_PARAMETER);
        /* And recursively call this entry method for each object */
        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.addStatement("$L.append(this.$L(o, $L))", SERIALIZED_OBJECT, ENTRY_METHOD, EVAL_PARAMETER);
            objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, ",");
        } else {
            objectMaskBuilder.addStatement("this.$L(o, $L)", ENTRY_METHOD, EVAL_PARAMETER);
        }
        objectMaskBuilder.endControlFlow();
        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.beginControlFlow("if ($L.length() > 1)", SERIALIZED_OBJECT);
            objectMaskBuilder.addStatement("$L.deleteCharAt($L.length() - 1)", SERIALIZED_OBJECT, SERIALIZED_OBJECT);
            objectMaskBuilder.endControlFlow();
            objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, "]");
            objectMaskBuilder.addStatement("return $L.toString()", SERIALIZED_OBJECT);
        }
        /* If it's not a Collection, then check if the Object is an array */
        objectMaskBuilder.nextControlFlow("else if ($L instanceof Object[])", OBJECT_PARAMETER);
        /* If it is, then iterate over the array */
        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.addStatement("$T $L = new $T($S)", StringBuilder.class, SERIALIZED_OBJECT, StringBuilder.class, "[");
        }
        objectMaskBuilder.beginControlFlow("for (Object o : ((Object[]) $L))", OBJECT_PARAMETER);
        /* And recursively call this entry method for each object */
        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.addStatement("$L.append(this.$L(o, $L))", SERIALIZED_OBJECT, ENTRY_METHOD, EVAL_PARAMETER);
            objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, ",");
        } else {
            objectMaskBuilder.addStatement("this.$L(o, $L)", ENTRY_METHOD, EVAL_PARAMETER);
        }
        objectMaskBuilder.endControlFlow();
        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.beginControlFlow("if ($L.length() > 1)", SERIALIZED_OBJECT);
            objectMaskBuilder.addStatement("$L.deleteCharAt($L.length() - 1)", SERIALIZED_OBJECT, SERIALIZED_OBJECT);
            objectMaskBuilder.endControlFlow();
            objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, "]");
            objectMaskBuilder.addStatement("return $L.toString()", SERIALIZED_OBJECT);
        }
        objectMaskBuilder.endControlFlow();
        objectMaskBuilder.endControlFlow();

        if (configuration.isNativeSerializationEnabled()) {
            objectMaskBuilder.returns(String.class);
            objectMaskBuilder.addStatement("return null");
        }

        cloakBuilder.addMethod(objectMaskBuilder.build());
    }
}
