package com.flipkart.masquerade.util;

/**
 * Created by shrey.garg on 12/05/17.
 */
public class FieldDescriptor {
    private final boolean primitive;
    private final boolean equatable;
    private final boolean comparable;
    private final boolean enumeration;

    public FieldDescriptor(boolean primitive, boolean equatable, boolean comparable, boolean enumeration) {
        this.primitive = primitive;
        this.equatable = equatable;
        this.comparable = comparable;
        this.enumeration = enumeration;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public boolean isEquatable() {
        return equatable;
    }

    public boolean isComparable() {
        return comparable;
    }

    public boolean isEnumeration() {
        return enumeration;
    }
}
