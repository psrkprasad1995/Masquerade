package com.flipkart.masquerade.processor;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.rule.Rule;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.Map;

import static com.flipkart.masquerade.util.Helper.getRuleInterface;
import static com.flipkart.masquerade.util.Strings.*;

/**
 * Processor which adds the entry point method in the Entry class
 * <p />
 * Created by shrey.garg on 12/05/17.
 */
public class RuleObjectProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public RuleObjectProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    /**
     * @param rule Rule for which the entry point will be added
     */
    public void addEntry(Rule rule) {
        MethodSpec.Builder objectMaskBuilder = MethodSpec.methodBuilder(ENTRY_METHOD);
        objectMaskBuilder.addModifiers(Modifier.PUBLIC);
        /* Adds an Object class parameter for which all the process at runtime will happen */
        objectMaskBuilder.addParameter(Object.class, OBJECT_PARAMETER);
        /* The second parameter refers to the Evaluator Object which will be used for comparisons */
        objectMaskBuilder.addParameter(rule.getEvaluatorClass(), EVAL_PARAMETER);

        /* If a null Object is passed, return immediately */
        objectMaskBuilder.beginControlFlow("if ($L == null)", OBJECT_PARAMETER);
        objectMaskBuilder.addStatement("return");
        objectMaskBuilder.endControlFlow();

        /* Fetch the Mask implementation object from Map and assign it to an interface reference for the Rule */
        objectMaskBuilder.addStatement("$T $L = this.$L.get($L.getClass().getName())",
                getRuleInterface(configuration, rule),
                MASKER_VARIABLE,
                rule.getName(),
                OBJECT_PARAMETER);

        /* Check if the retrieved Object is present */
        objectMaskBuilder.beginControlFlow("if ($L != null)", MASKER_VARIABLE);
        /* If it is, then call the mask method for the Object */
        objectMaskBuilder.addStatement("$L.$L($L, $L, this)", MASKER_VARIABLE, INTERFACE_METHOD, OBJECT_PARAMETER, EVAL_PARAMETER);
        objectMaskBuilder.nextControlFlow("else");
        /* Otherwise, check if the Object is an instance of Map */
        objectMaskBuilder.beginControlFlow("if ($L instanceof $T)", OBJECT_PARAMETER, Map.class);
        /* If it is, then recursively call this entry method with the List of map values */
        objectMaskBuilder.addStatement("this.$L(((Map) $L).values(), $L)", ENTRY_METHOD, OBJECT_PARAMETER, EVAL_PARAMETER);

        /* If it's not a Map, then check if the Object is a collection */
        objectMaskBuilder.nextControlFlow("else if ($L instanceof $T)", OBJECT_PARAMETER, Collection.class);
        /* If it is, then iterate over the collection */
        objectMaskBuilder.beginControlFlow("for (Object o : ((Collection) $L))", OBJECT_PARAMETER);
        /* And recursively call this entry method for each object */
        objectMaskBuilder.addStatement("this.$L(o, $L)", ENTRY_METHOD, EVAL_PARAMETER);
        objectMaskBuilder.endControlFlow();
        objectMaskBuilder.endControlFlow();
        objectMaskBuilder.endControlFlow();

        cloakBuilder.addMethod(objectMaskBuilder.build());
    }
}
