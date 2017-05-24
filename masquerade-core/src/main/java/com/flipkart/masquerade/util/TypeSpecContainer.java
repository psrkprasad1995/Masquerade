package com.flipkart.masquerade.util;

import com.squareup.javapoet.TypeSpec;

/**
 * Created by shrey.garg on 12/05/17.
 */
public class TypeSpecContainer {
    private final String packagePath;
    private final TypeSpec spec;

    public TypeSpecContainer(String packagePath, TypeSpec spec) {
        this.packagePath = packagePath;
        this.spec = spec;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public TypeSpec getSpec() {
        return spec;
    }
}
