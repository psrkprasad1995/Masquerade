package com.flipkart.masquerade.util;

/**
 * Created by shrey.garg on 18/07/17.
 */
public class FallbackSpecification {
    private Class<?> clazz;
    private String staticMethod;

    public FallbackSpecification(Class<?> clazz, String staticMethod) {
        this.clazz = clazz;
        this.staticMethod = staticMethod;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getStaticMethod() {
        return staticMethod;
    }

    public void setStaticMethod(String staticMethod) {
        this.staticMethod = staticMethod;
    }
}
