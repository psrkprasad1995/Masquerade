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
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeSpec;

import static com.flipkart.masquerade.util.Helper.getToStringVariableName;

/**
 * Created by shrey.garg on 16/07/17.
 */
public class ToStringInitializationProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public ToStringInitializationProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    /**
     * @param rule The rule to generate the entries for
     */
    public void generateToStringEntries(Rule rule, CodeBlock.Builder initializer) {
        for (String toStringSerializable : configuration.toStringSerializableClasses()) {
            initializer.addStatement("$L.put($S, $L)", rule.getName(), toStringSerializable, getToStringVariableName(rule));
        }
    }
}
