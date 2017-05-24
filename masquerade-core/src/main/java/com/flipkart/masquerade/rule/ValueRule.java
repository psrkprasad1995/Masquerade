package com.flipkart.masquerade.rule;

/**
 * Created by shrey.garg on 24/04/17.
 */
public class ValueRule {
    private final String annotationMember;
    private final Operator operator;
    private final String evaluatorFunction;
    private final boolean defaultIgnored;

    public ValueRule(String annotationMember, Operator operator, String evaluatorFunction) {
        this(annotationMember, operator, evaluatorFunction, true);
    }

    public ValueRule(String annotationMember, Operator operator, String evaluatorFunction, boolean defaultIgnored) {
        if (annotationMember == null || operator == null || evaluatorFunction == null) {
            throw new NullPointerException("ValueRule class does not accept any null parameters");
        }

        this.annotationMember = annotationMember;
        this.operator = operator;
        this.evaluatorFunction = evaluatorFunction;
        this.defaultIgnored = defaultIgnored;
    }

    public String getAnnotationMember() {
        return annotationMember;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getEvaluatorFunction() {
        return evaluatorFunction;
    }

    public boolean isDefaultIgnored() {
        return defaultIgnored;
    }
}
