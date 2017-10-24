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

import java.io.IOException;
import java.util.Map;

import static com.flipkart.masquerade.util.Helper.getMapImplementationName;
import static com.flipkart.masquerade.util.Strings.*;

/**
 * Created by shrey.garg on 24/07/17.
 */
public class MapOverrideProcessor extends BaseOverrideProcessor {
    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder  Entry class under construction for the cycle
     */
    public MapOverrideProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        super(configuration, cloakBuilder);
    }

    /**
     * @param rule The rule which is being processed
     * @return A fully constructed TypeSpec object for the enum implementation
     */
    public TypeSpec createOverride(Rule rule) {
        String implName = getMapImplementationName(rule);
        MethodSpec.Builder methodBuilder = generateOverrideMethod(rule, Map.class);

        if (configuration.isNativeSerializationEnabled()) {

            methodBuilder.addStatement("$L.writeStartObject();", SERIALIZED_OBJECT);

            methodBuilder.addStatement(
                    "$L.forEach((k, v) -> { try { $L.writeFieldName(String.valueOf(k)); $L.$L(v, $L, $L); } catch ($T e) { e.printStackTrace(); }})",
                    OBJECT_PARAMETER, SERIALIZED_OBJECT, CLOAK_PARAMETER, ENTRY_METHOD, EVAL_PARAMETER, SERIALIZED_OBJECT, IOException.class);

            methodBuilder.addStatement("$L.writeEndObject();", SERIALIZED_OBJECT);
        } else {
            methodBuilder.addStatement("$L.$L($L.values(), $L)", CLOAK_PARAMETER, ENTRY_METHOD, OBJECT_PARAMETER, EVAL_PARAMETER);
        }

        return generateImplementationType(rule, Map.class, implName, methodBuilder.build());
    }
}
