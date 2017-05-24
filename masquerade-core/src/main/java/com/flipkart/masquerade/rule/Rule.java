package com.flipkart.masquerade.rule;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by shrey.garg on 24/04/17.
 */
public class Rule {
    private final String name;
    private final Class<? extends Annotation> annotationClass;
    private final Class<?> evaluatorClass;
    private final List<ValueRule> valueRules;

    public Rule(String name, Class<? extends Annotation> annotationClass, Class<?> evaluatorClass, List<ValueRule> valueRules) {
        if (name == null || annotationClass == null || evaluatorClass == null || valueRules == null) {
            throw new NullPointerException("Rule class does not accept any null parameters");
        }

        this.name = name;
        this.annotationClass = annotationClass;
        this.evaluatorClass = evaluatorClass;
        this.valueRules = valueRules;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public Class<?> getEvaluatorClass() {
        return evaluatorClass;
    }

    public List<ValueRule> getValueRules() {
        return valueRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rule)) return false;

        Rule rule = (Rule) o;

        return name.equals(rule.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
