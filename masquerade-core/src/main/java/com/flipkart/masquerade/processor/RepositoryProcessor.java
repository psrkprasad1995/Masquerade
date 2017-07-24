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
import com.flipkart.masquerade.processor.type.NoOpInitializationProcessor;
import com.flipkart.masquerade.processor.type.ToStringInitializationProcessor;
import com.flipkart.masquerade.rule.Rule;
import com.flipkart.masquerade.util.EntryType;
import com.flipkart.masquerade.util.RepositoryEntry;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.List;

import static com.flipkart.masquerade.util.Helper.*;
import static com.flipkart.masquerade.util.Strings.SET_CLASS;
import static com.flipkart.masquerade.util.Strings.SET_PARAMETER;

/**
 * Created by shrey.garg on 24/07/17.
 */
public class RepositoryProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    private final NoOpInitializationProcessor noOpInitializationProcessor;
    private final ToStringInitializationProcessor toStringInitializationProcessor;
    private final ReferenceMapProcessor mapProcessor;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public RepositoryProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
        this.noOpInitializationProcessor = new NoOpInitializationProcessor(configuration, cloakBuilder);
        this.toStringInitializationProcessor = new ToStringInitializationProcessor(configuration, cloakBuilder);
        this.mapProcessor = new ReferenceMapProcessor(configuration, cloakBuilder);
    }

    public void createReference() {
        ClassName className = getRepositoryClass(configuration);
        cloakBuilder.addField(FieldSpec
                .builder(className, SET_PARAMETER, Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T()", className).build());
    }

    public TypeSpec createRepository(List<RepositoryEntry> repositoryEntries) {
        /* Start construction of the repository class which will be used by the user */
        final TypeSpec.Builder repositoryBuilder = TypeSpec.classBuilder(SET_CLASS);
        repositoryBuilder.addModifiers(Modifier.PUBLIC);

        final CodeBlock.Builder initializer = CodeBlock.builder();

        for (Rule rule : configuration.getRules()) {
            /* Creates a Map of Class name and Mask */
            mapProcessor.addMap(rule, repositoryBuilder);
            /* Create a NoOP Mask field */
            FieldSpec fieldSpec = FieldSpec.builder(getRuleInterface(configuration, rule), getNoOpVariableName(rule), Modifier.PRIVATE)
                    .initializer("new $T()", getNoOpImplementationClass(configuration, rule)).build();
            repositoryBuilder.addField(fieldSpec);
            FieldSpec enumFieldSpec = FieldSpec.builder(getRuleInterface(configuration, rule), getEnumVariableName(rule), Modifier.PRIVATE)
                    .initializer("new $T()", getEnumImplementationClass(configuration, rule)).build();
            repositoryBuilder.addField(enumFieldSpec);
            FieldSpec toStringFieldSpec = FieldSpec.builder(getRuleInterface(configuration, rule), getToStringVariableName(rule), Modifier.PRIVATE)
                    .initializer("new $T()", getToStringImplementationClass(configuration, rule)).build();
            repositoryBuilder.addField(toStringFieldSpec);

            handleNoOpEntries(rule, initializer);
            handleToStringEntries(rule, initializer);
            handleProcessedEntries(initializer, repositoryEntries);
        }

        repositoryBuilder.addInitializerBlock(initializer.build());
        return repositoryBuilder.build();
    }

    private void handleToStringEntries(Rule rule, CodeBlock.Builder initializer) {
        toStringInitializationProcessor.generateToStringEntries(rule)
                .forEach(re -> initializer.addStatement("$L.put($S, $L)", rule.getName(), re.getClazz().getName(), getToStringVariableName(rule)));
    }

    private void handleNoOpEntries(Rule rule, CodeBlock.Builder initializer) {
        noOpInitializationProcessor.generateNoOpEntries(rule)
                .forEach(re -> initializer.addStatement("$L.put($S, $L)", rule.getName(), re.getClazz().getName(), getNoOpVariableName(rule)));
    }

    private void handleProcessedEntries(CodeBlock.Builder initializer, List<RepositoryEntry> repositoryEntries) {
        for (RepositoryEntry entry : repositoryEntries) {
            if (entry.getEntryType() == EntryType.NEW) {
                String implName = getImplementationName(entry.getRule(), entry.getClazz());
                ClassName cloakName = ClassName.get(getImplementationPackage(configuration, entry.getClazz()), implName);
                initializer.addStatement("$L.put($S, new $T())", entry.getRule().getName(), entry.getClazz().getName(), cloakName);
            } else if (entry.getEntryType() == EntryType.ENUM) {
                initializer.addStatement("$L.put($S, $L)", entry.getRule().getName(), entry.getClazz().getName(), getEnumVariableName(entry.getRule()));
            } else if (entry.getEntryType() == EntryType.NoOP) {
                initializer.addStatement("$L.put($S, $L)", entry.getRule().getName(), entry.getClazz().getName(), getNoOpVariableName(entry.getRule()));
            }
        }
    }
}
