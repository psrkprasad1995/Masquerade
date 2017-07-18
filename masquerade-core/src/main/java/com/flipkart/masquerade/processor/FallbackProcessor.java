package com.flipkart.masquerade.processor;

import com.flipkart.masquerade.Configuration;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import static com.flipkart.masquerade.util.Strings.DEBUG_LIST;
import static com.flipkart.masquerade.util.Strings.OBJECT_PARAMETER;

/**
 * Created by shrey.garg on 18/07/17.
 */
public class FallbackProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public FallbackProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    public void addFallbackCall(MethodSpec.Builder objectMaskBuilder) {
        if (configuration.fallbackFunction() == null) {
            return;
        }

        objectMaskBuilder.nextControlFlow("else");
        objectMaskBuilder.addStatement("return $T.$L($L)", configuration.fallbackFunction().getClazz(), configuration.fallbackFunction().getStaticMethod(), OBJECT_PARAMETER);

    }
}
