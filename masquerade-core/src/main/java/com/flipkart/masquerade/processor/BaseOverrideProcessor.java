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
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import static com.flipkart.masquerade.util.Helper.getEntryClass;
import static com.flipkart.masquerade.util.Helper.getRuleInterface;
import static com.flipkart.masquerade.util.Strings.*;

/**
 * Created by shrey.garg on 27/05/17.
 */
public abstract class BaseOverrideProcessor {
    protected final Configuration configuration;
    protected final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public BaseOverrideProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    /**
     * @param rule Current Rule
     * @param clazz Current Class
     * @return A MethodSpec builder which overrides the interface method
     */
    protected MethodSpec.Builder generateOverrideMethod(Rule rule, Class<?> clazz) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(INTERFACE_METHOD);
        methodBuilder.addAnnotation(Override.class);
        methodBuilder.addModifiers(Modifier.PUBLIC);
        methodBuilder.addParameter(clazz, OBJECT_PARAMETER);
        methodBuilder.addParameter(rule.getEvaluatorClass(), EVAL_PARAMETER);
        methodBuilder.addParameter(getEntryClass(configuration), CLOAK_PARAMETER);

        if (configuration.isNativeSerializationEnabled()) {
            methodBuilder.returns(String.class);
        }

        return methodBuilder;
    }

    /**
     * @param rule Current Rule
     * @param clazz Current Class
     * @param implName Name of the implementation class
     * @param method The overridden method the implementation class
     * @return Constructed TypeSpec for the implementation class
     */
    protected TypeSpec generateImplementationType(Rule rule, Class<?> clazz, String implName, MethodSpec method) {
        TypeSpec.Builder implBuilder = TypeSpec.classBuilder(implName);
        implBuilder.addModifiers(Modifier.PUBLIC);
        /* Implements the interface and attaches the current class as a Generic bound */
        implBuilder.addSuperinterface(ParameterizedTypeName.get(getRuleInterface(configuration, rule), TypeName.get(clazz)));
        implBuilder.addMethod(method);
        return implBuilder.build();
    }

}
