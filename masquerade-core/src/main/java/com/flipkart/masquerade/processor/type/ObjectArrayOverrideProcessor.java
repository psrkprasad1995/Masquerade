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

import static com.flipkart.masquerade.util.Helper.getObjectArrayImplementationName;
import static com.flipkart.masquerade.util.Strings.*;

/**
 * Created by shrey.garg on 24/07/17.
 */
public class ObjectArrayOverrideProcessor extends BaseOverrideProcessor {
    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder  Entry class under construction for the cycle
     */
    public ObjectArrayOverrideProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        super(configuration, cloakBuilder);
    }

    /**
     * @param rule The rule which is being processed
     * @return A fully constructed TypeSpec object for the enum implementation
     */
    public TypeSpec createOverride(Rule rule) {
        String implName = getObjectArrayImplementationName(rule);
        MethodSpec.Builder methodBuilder = generateOverrideMethod(rule, ArrayTypeName.of(Object.class));

        if (configuration.isNativeSerializationEnabled()) {
            methodBuilder.addStatement("$L.writeStartArray()", SERIALIZED_OBJECT);
            methodBuilder.beginControlFlow("for (Object o : $L)", OBJECT_PARAMETER);
            methodBuilder.addStatement("$L.$L(o, $L, $L)", CLOAK_PARAMETER, ENTRY_METHOD, EVAL_PARAMETER, SERIALIZED_OBJECT);
            methodBuilder.endControlFlow();
            methodBuilder.addStatement("$L.writeEndArray()", SERIALIZED_OBJECT);
        } else {
            methodBuilder.beginControlFlow("for (Object o : $L)", OBJECT_PARAMETER);
            /* And recursively call this entry method for each object */
            methodBuilder.addStatement("$L.$L(o, $L)", CLOAK_PARAMETER, ENTRY_METHOD, EVAL_PARAMETER);
            methodBuilder.endControlFlow();
        }

        return generateImplementationType(rule, ArrayTypeName.of(Object.class), implName, methodBuilder.build());
    }
}
