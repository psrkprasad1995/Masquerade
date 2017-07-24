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

package com.flipkart.masquerade.processor.type;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.rule.Rule;
import com.flipkart.masquerade.util.EntryType;
import com.flipkart.masquerade.util.RepositoryEntry;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import static com.flipkart.masquerade.util.Helper.getWrapperTypes;

/**
 * Processor that add entries for all Primitive wrappers and String as NoOp Masks
 * <p />
 * Created by shrey.garg on 27/05/17.
 */
public class NoOpInitializationProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public NoOpInitializationProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    /**
     * @param rule The rule to generate the entries for
     */
    public List<RepositoryEntry> generateNoOpEntries(Rule rule) {
        List<RepositoryEntry> repositoryEntries = new ArrayList<>();
        for (Class<?> clazz : getWrapperTypes()) {
            repositoryEntries.add(new RepositoryEntry(rule, clazz, EntryType.NoOP));
        }
        repositoryEntries.add(new RepositoryEntry(rule, String.class, EntryType.NoOP));
        return repositoryEntries;
    }
}
