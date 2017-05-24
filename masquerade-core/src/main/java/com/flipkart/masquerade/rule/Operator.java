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
