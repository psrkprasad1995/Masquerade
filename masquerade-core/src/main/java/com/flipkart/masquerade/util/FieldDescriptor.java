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
