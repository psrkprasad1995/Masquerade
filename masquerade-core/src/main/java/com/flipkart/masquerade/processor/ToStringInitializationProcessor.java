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
