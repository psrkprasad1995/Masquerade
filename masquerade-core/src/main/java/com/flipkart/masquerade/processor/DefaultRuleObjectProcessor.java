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
