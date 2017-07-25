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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.flipkart.masquerade.util.Helper.getPrimitivesTypes;
import static com.flipkart.masquerade.util.Strings.*;

/**
 * Created by shrey.garg on 23/07/17.
 */
public class SerializationRuleObjectProcessor extends RuleObjectProcessor {
    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder  Entry class under construction for the cycle
     */
    public SerializationRuleObjectProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        super(configuration, cloakBuilder);
    }

    @Override
    protected void handleReturnsForNullObjects(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.addStatement("return null");
    }

    @Override
    protected void handleRegisteredClasses(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.addStatement("return $L.$L($L, $L, this)", MASKER_VARIABLE, INTERFACE_METHOD, OBJECT_PARAMETER, EVAL_PARAMETER);
    }

    @Override
    protected void handleMaps(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.addStatement("$T $L = new $T($S)", StringBuilder.class, SERIALIZED_OBJECT, StringBuilder.class, "{");

        objectMaskBuilder.addStatement(
                "(($T) $L).forEach((k, v) -> $L.append($S).append(k).append($S).append($S).append(this.$L(v, $L)).append($S))",
                Map.class, OBJECT_PARAMETER, SERIALIZED_OBJECT, QUOTES, QUOTES, ":", ENTRY_METHOD, EVAL_PARAMETER, ",");

        objectMaskBuilder.beginControlFlow("if ($L.length() > 1)", SERIALIZED_OBJECT);
        objectMaskBuilder.addStatement("$L.deleteCharAt($L.length() - 1)", SERIALIZED_OBJECT, SERIALIZED_OBJECT);
        objectMaskBuilder.endControlFlow();
        objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, "}");
        objectMaskBuilder.addStatement("return $L.toString()", SERIALIZED_OBJECT);
    }

    @Override
    protected void handleCollections(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.addStatement("$T $L = new $T($S)", StringBuilder.class, SERIALIZED_OBJECT, StringBuilder.class, "[");
        objectMaskBuilder.beginControlFlow("for (Object o : ((Collection) $L))", OBJECT_PARAMETER);
        /* And recursively call this entry method for each object */
        objectMaskBuilder.addStatement("$L.append(this.$L(o, $L))", SERIALIZED_OBJECT, ENTRY_METHOD, EVAL_PARAMETER);
        objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, ",");
        objectMaskBuilder.endControlFlow();
        objectMaskBuilder.beginControlFlow("if ($L.length() > 1)", SERIALIZED_OBJECT);
        objectMaskBuilder.addStatement("$L.deleteCharAt($L.length() - 1)", SERIALIZED_OBJECT, SERIALIZED_OBJECT);
        objectMaskBuilder.endControlFlow();
        objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, "]");
        objectMaskBuilder.addStatement("return $L.toString()", SERIALIZED_OBJECT);
    }

    @Override
    protected void handleObjectArrays(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.addStatement("$T $L = new $T($S)", StringBuilder.class, SERIALIZED_OBJECT, StringBuilder.class, "[");
        objectMaskBuilder.beginControlFlow("for (Object o : ((Object[]) $L))", OBJECT_PARAMETER);
        /* And recursively call this entry method for each object */
        objectMaskBuilder.addStatement("$L.append(this.$L(o, $L))", SERIALIZED_OBJECT, ENTRY_METHOD, EVAL_PARAMETER);
        objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, ",");
        objectMaskBuilder.endControlFlow();
        objectMaskBuilder.beginControlFlow("if ($L.length() > 1)", SERIALIZED_OBJECT);
        objectMaskBuilder.addStatement("$L.deleteCharAt($L.length() - 1)", SERIALIZED_OBJECT, SERIALIZED_OBJECT);
        objectMaskBuilder.endControlFlow();
        objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, "]");
        objectMaskBuilder.addStatement("return $L.toString()", SERIALIZED_OBJECT);
    }

    @Override
    protected void handlePrimitiveArrays(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.nextControlFlow("else if ($L.getClass().isArray())", OBJECT_PARAMETER);
        /* Handle char[] separately */
        objectMaskBuilder.beginControlFlow("if ($L instanceof $T[])", OBJECT_PARAMETER, Character.TYPE);
        objectMaskBuilder.addStatement("return $S + new $T(($T[]) $L) + $S", QUOTES, String.class, Character.TYPE, OBJECT_PARAMETER, QUOTES);
        /* Need to check for all primitive types individually */
        List<Class<?>> primitiveTypes = new ArrayList<>(getPrimitivesTypes());
        for (int i = 0; i < primitiveTypes.size(); i++) {
            Class<?> primitiveType = primitiveTypes.get(i);
            objectMaskBuilder.nextControlFlow("else if ($L instanceof $T[])", OBJECT_PARAMETER, primitiveType);
            /* Iterate over the array */
            objectMaskBuilder.addStatement("$T $L = new $T($S)", StringBuilder.class, SERIALIZED_OBJECT, StringBuilder.class, "[");
            objectMaskBuilder.beginControlFlow("for ($T o : (($T[]) $L))", primitiveType, primitiveType, OBJECT_PARAMETER);
            /* And recursively call this entry method for each object */
            objectMaskBuilder.addStatement("$L.append(this.$L(o, $L))", SERIALIZED_OBJECT, ENTRY_METHOD, EVAL_PARAMETER);
            objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, ",");
            objectMaskBuilder.endControlFlow();
            objectMaskBuilder.beginControlFlow("if ($L.length() > 1)", SERIALIZED_OBJECT);
            objectMaskBuilder.addStatement("$L.deleteCharAt($L.length() - 1)", SERIALIZED_OBJECT, SERIALIZED_OBJECT);
            objectMaskBuilder.endControlFlow();
            objectMaskBuilder.addStatement("$L.append($S)", SERIALIZED_OBJECT, "]");
            objectMaskBuilder.addStatement("return $L.toString()", SERIALIZED_OBJECT);
        }
        objectMaskBuilder.endControlFlow();
    }

    @Override
    protected void handleReturns(MethodSpec.Builder objectMaskBuilder) {
        objectMaskBuilder.returns(String.class);
        objectMaskBuilder.addStatement("return null");
    }
}
