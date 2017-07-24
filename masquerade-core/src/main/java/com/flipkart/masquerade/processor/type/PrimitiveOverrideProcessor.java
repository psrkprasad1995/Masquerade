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

package com.flipkart.masquerade.processor.type;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.processor.BaseOverrideProcessor;
import com.flipkart.masquerade.rule.Rule;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import static com.flipkart.masquerade.util.Helper.getPrimitiveImplementationName;
import static com.flipkart.masquerade.util.Helper.getWrapperTypes;
import static com.flipkart.masquerade.util.Strings.OBJECT_PARAMETER;
import static com.flipkart.masquerade.util.Strings.QUOTES;

/**
 * Created by shrey.garg on 24/07/17.
 */
public class PrimitiveOverrideProcessor extends BaseOverrideProcessor {
    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder  Entry class under construction for the cycle
     */
    public PrimitiveOverrideProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        super(configuration, cloakBuilder);
    }

    /**
     * @param rule The rule which is being processed
     * @return A fully constructed TypeSpec object for the enum implementation
     */
    public List<TypeSpec> createOverrides(Rule rule) {
        List<TypeSpec> typeSpecs = new ArrayList<>();
        for (Class<?> primitiveType : getWrapperTypes()) {
            String implName = getPrimitiveImplementationName(rule, primitiveType);
            MethodSpec.Builder methodBuilder = generateOverrideMethod(rule, primitiveType);

            if (primitiveType.equals(Character.class)) {
                methodBuilder.beginControlFlow("if ($L == Character.valueOf('\\u0000'))", OBJECT_PARAMETER);
                methodBuilder.addStatement("return $S", QUOTES + "\\u0000" + QUOTES);
                methodBuilder.endControlFlow();
                methodBuilder.addStatement("return $S + String.valueOf((char) $L) + $S", QUOTES, OBJECT_PARAMETER, QUOTES);
            } else {
                methodBuilder.addStatement("return String.valueOf($L)", OBJECT_PARAMETER);
            }

            typeSpecs.add(generateImplementationType(rule, primitiveType, implName, methodBuilder.build()));
        }
        return typeSpecs;
    }
}
