package com.flipkart.masquerade.util;

/**
 * Created by shrey.garg on 12/05/17.
 */
public class Strings {
    public static final String MASKER_VARIABLE = "masker";

    public static final String INTERFACE_SUFFIX = "Mask";

    public static final String ENTRY_CLASS = "Cloak";
    public static final String ENTRY_METHOD = "hide";

    public static final String INTERFACE_METHOD = INTERFACE_SUFFIX.toLowerCase();
    public static final String OBJECT_PARAMETER = Object.class.getSimpleName().toLowerCase();
    public static final String EVAL_PARAMETER = "eval";
    public static final String CLOAK_PARAMETER = ENTRY_CLASS.toLowerCase();
}
