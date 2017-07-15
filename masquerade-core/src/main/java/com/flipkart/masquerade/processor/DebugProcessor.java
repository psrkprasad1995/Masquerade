package com.flipkart.masquerade.processor;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.util.Strings;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.List;

import static com.flipkart.masquerade.util.Strings.DEBUG_LIST;
import static com.flipkart.masquerade.util.Strings.OBJECT_PARAMETER;

/**
 * Created by shrey.garg on 15/07/17.
 */
public class DebugProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public DebugProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    public void addConstructor() {
        if (!configuration.isDebugMode()) {
            return;
        }

        cloakBuilder.addField(ParameterizedTypeName.get(List.class, String.class), DEBUG_LIST, Modifier.PRIVATE, Modifier.FINAL);
        cloakBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(List.class, String.class), DEBUG_LIST, Modifier.FINAL)
                .addStatement("this.$L = $L", DEBUG_LIST, DEBUG_LIST).build());
    }

    public void addDebugCollector(MethodSpec.Builder objectMaskBuilder) {
        if (!configuration.isDebugMode()) {
            return;
        }

        objectMaskBuilder.nextControlFlow("else");
        objectMaskBuilder.addStatement("$L.add($L.getClass().getName())", DEBUG_LIST, OBJECT_PARAMETER);
    }
}
