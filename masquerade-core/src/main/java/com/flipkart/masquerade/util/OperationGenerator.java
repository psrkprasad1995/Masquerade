package com.flipkart.masquerade.util;

/**
 * Created by shrey.garg on 12/05/17.
 */
public class OperationGenerator {
    public static void processEquals(StringBuilder operation, FieldDescriptor descriptor) {
        if (descriptor.isEquatable()) {
            operation.append("($L == $L)");
        } else {
            operation.append("($L.equals($S))");
        }
    }

    public static void processNotEquals(StringBuilder operation, FieldDescriptor descriptor) {
        if (descriptor.isEquatable()) {
            operation.append("($L != $L)");
        } else {
            operation.append("(!$L.equals($L))");
        }
    }

    public static void processGreaterThan(StringBuilder operation, FieldDescriptor descriptor) {
        if (descriptor.isPrimitive()) {
            operation.append("($L > $L)");
        } else if (descriptor.isComparable()) {
            operation.append("($L.compareTo($L) > 0)");
        } else {
            throw new UnsupportedOperationException("Cannot compare non-comparable types");
        }
    }

    public static void processGreaterThanEquals(StringBuilder operation, FieldDescriptor descriptor) {
        if (descriptor.isPrimitive()) {
            operation.append("($L >= $L)");
        } else if (descriptor.isComparable()) {
            operation.append("($L.compareTo($L) >= 0)");
        } else {
            throw new UnsupportedOperationException("Cannot compare non-comparable types");
        }
    }

    public static void processLesserThan(StringBuilder operation, FieldDescriptor descriptor) {
        if (descriptor.isPrimitive()) {
            operation.append("($L < $L)");
        } else if (descriptor.isComparable()) {
            operation.append("($L.compareTo($L) < 0)");
        } else {
            throw new UnsupportedOperationException("Cannot compare non-comparable types");
        }
    }

    public static void processLesserThanEquals(StringBuilder operation, FieldDescriptor descriptor) {
        if (descriptor.isPrimitive()) {
            operation.append("($L <= $L)");
        } else if (descriptor.isComparable()) {
            operation.append("($L.compareTo($L) <= 0)");
        } else {
            throw new UnsupportedOperationException("Cannot compare non-comparable types");
        }
    }
}
