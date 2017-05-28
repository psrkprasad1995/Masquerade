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

package com.flipkart.masquerade.rule;

import com.flipkart.masquerade.util.FieldDescriptor;
import com.flipkart.masquerade.util.OperationGenerator;

import java.util.function.BiConsumer;

/**
 * Created by shrey.garg on 24/04/17.
 */
public enum Operator {
    EQUAL(OperationGenerator::processEquals),
    GREATER(OperationGenerator::processGreaterThan),
    GREATER_EQUAL(OperationGenerator::processGreaterThanEquals),
    LESSER(OperationGenerator::processLesserThan),
    LESSER_EQUAL(OperationGenerator::processLesserThanEquals),
    UNEQUAL(OperationGenerator::processNotEquals);

    private final BiConsumer<StringBuilder, FieldDescriptor> generateOperation;

    Operator(BiConsumer<StringBuilder, FieldDescriptor> generateOperation) {
        this.generateOperation = generateOperation;
    }

    public BiConsumer<StringBuilder, FieldDescriptor> getGenerateOperation() {
        return generateOperation;
    }
}
