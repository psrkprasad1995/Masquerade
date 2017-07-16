package com.flipkart.masquerade.processor;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.rule.Rule;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import static com.flipkart.masquerade.util.Helper.getToStringImplementationName;
import static com.flipkart.masquerade.util.Strings.OBJECT_PARAMETER;
import static com.flipkart.masquerade.util.Strings.QUOTES;

/**
 * Created by shrey.garg on 16/07/17.
 */
public class ToStringProcessor extends BaseOverrideProcessor {
    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder  Entry class under construction for the cycle
     */
    public ToStringProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        super(configuration, cloakBuilder);
    }

    /**
     * @param rule The rule which is being processed
     * @return A fully constructed TypeSpec object for the enum implementation
     */
    public TypeSpec createOverride(Rule rule) {
        String implName = getToStringImplementationName(rule);
        MethodSpec.Builder methodBuilder = generateOverrideMethod(rule, Object.class);

        if (configuration.isNativeSerializationEnabled()) {
            methodBuilder.addStatement("return $S + $L.toString() + $S", QUOTES, OBJECT_PARAMETER, QUOTES);
        }

        return generateImplementationType(rule, Object.class, implName, methodBuilder.build());
    }
}
