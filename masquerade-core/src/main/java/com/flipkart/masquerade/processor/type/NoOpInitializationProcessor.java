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
import com.flipkart.masquerade.rule.Rule;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeSpec;

import static com.flipkart.masquerade.util.Helper.getNoOpVariableName;
import static com.flipkart.masquerade.util.Helper.getWrapperTypes;

/**
 * Processor that add entries for all Primitive wrappers and String as NoOp Masks
 * <p />
 * Created by shrey.garg on 27/05/17.
 */
public class NoOpInitializationProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public NoOpInitializationProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    /**
     * @param rule The rule to generate the entries for
     */
    public void generateNoOpEntries(Rule rule, CodeBlock.Builder initializer) {
        for (Class<?> clazz : getWrapperTypes()) {
            initializer.addStatement("$L.put($S, $L)", rule.getName(), clazz.getName(), getNoOpVariableName(rule));
        }
        initializer.addStatement("$L.put($S, $L)", rule.getName(), String.class.getName(), getNoOpVariableName(rule));
    }
}
