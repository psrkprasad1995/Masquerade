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
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import static com.flipkart.masquerade.util.Helper.getPrimitiveArrayImplementationName;
import static com.flipkart.masquerade.util.Helper.getPrimitivesTypes;
import static com.flipkart.masquerade.util.Strings.*;

/**
 * Created by shrey.garg on 24/07/17.
 */
public class PrimitiveArrayOverrideProcessor extends BaseOverrideProcessor {
    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder  Entry class under construction for the cycle
     */
    public PrimitiveArrayOverrideProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        super(configuration, cloakBuilder);
    }

    /**
     * @param rule The rule which is being processed
     * @return A fully constructed TypeSpec object for the enum implementation
     */
    public List<TypeSpec> createOverrides(Rule rule) {
        List<TypeSpec> typeSpecs = new ArrayList<>();
        for (Class<?> primitiveType : getPrimitivesTypes()) {
            String implName = getPrimitiveArrayImplementationName(rule, primitiveType);
            MethodSpec.Builder methodBuilder = generateOverrideMethod(rule, ArrayTypeName.of(primitiveType));

            if (configuration.isNativeSerializationEnabled()) {
                methodBuilder.addStatement("$L.writeStartArray()", SERIALIZED_OBJECT);
                methodBuilder.beginControlFlow("for ($T o : $L)", primitiveType, OBJECT_PARAMETER);
                methodBuilder.addStatement("$L.$L(o, $L, $L)", CLOAK_PARAMETER, ENTRY_METHOD, EVAL_PARAMETER, SERIALIZED_OBJECT);
                methodBuilder.endControlFlow();
                methodBuilder.addStatement("$L.writeEndArray()", SERIALIZED_OBJECT);
            }

            typeSpecs.add(generateImplementationType(rule, ArrayTypeName.of(primitiveType), implName, methodBuilder.build()));
        }
        return typeSpecs;
    }
}
