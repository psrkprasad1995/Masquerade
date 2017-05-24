package com.flipkart.masquerade.processor;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.rule.Rule;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;

import static com.flipkart.masquerade.util.Helper.getRuleInterface;

/**
 * Processor that adds and initializes a Map field mapping Class to Mask Object.
 * <p />
 * Created by shrey.garg on 12/05/17.
 */
public class ReferenceMapProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public ReferenceMapProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    /**
     * Adds a private field and initializes it as well.
     *
     * @param rule The rule for which the Map will be generated
     */
    public void addMap(Rule rule) {
        ParameterizedTypeName mapType = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), getRuleInterface(configuration, rule));
        cloakBuilder.addField(FieldSpec.builder(mapType, rule.getName(), Modifier.PRIVATE).initializer("new $T<>()", HashMap.class).build());
    }

}
