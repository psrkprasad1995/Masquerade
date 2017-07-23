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
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import static com.flipkart.masquerade.util.Strings.*;

/**
 * Created by shrey.garg on 23/07/17.
 */
public class DefaultRuleObjectProcessor extends RuleObjectProcessor {
    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder  Entry class under construction for the cycle
     */
    public DefaultRuleObjectProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        super(configuration, cloakBuilder);
    }

    @Override
    protected void handleReturnsForNullObjects(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.addStatement("return");
    }

    @Override
    protected void handleRegisteredClasses(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.addStatement("$L.$L($L, $L, this)", MASKER_VARIABLE, INTERFACE_METHOD, OBJECT_PARAMETER, EVAL_PARAMETER);
    }

    @Override
    protected void handleMaps(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.addStatement("this.$L(((Map) $L).values(), $L)", ENTRY_METHOD, OBJECT_PARAMETER, EVAL_PARAMETER);
    }

    @Override
    protected void handleCollections(MethodSpec.Builder objectMaskBuilder) {
        /* And recursively call this entry method for each object */
        objectMaskBuilder.beginControlFlow("for (Object o : ((Collection) $L))", OBJECT_PARAMETER);
        objectMaskBuilder.addStatement("this.$L(o, $L)", ENTRY_METHOD, EVAL_PARAMETER);
        objectMaskBuilder.endControlFlow();
    }

    @Override
    protected void handleObjectArrays(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.beginControlFlow("for (Object o : ((Object[]) $L))", OBJECT_PARAMETER);
        /* And recursively call this entry method for each object */
        objectMaskBuilder.addStatement("this.$L(o, $L)", ENTRY_METHOD, EVAL_PARAMETER);
        objectMaskBuilder.endControlFlow();
    }

    @Override
    protected void handlePrimitiveArrays(MethodSpec.Builder objectMaskBuilder) {
        // Nothing needs to be done in case of primitive arrays in this case
    }

    @Override
    protected void handleReturns(MethodSpec.Builder objectMaskBuilder) {
        // Returns nothing in this case
    }
}
