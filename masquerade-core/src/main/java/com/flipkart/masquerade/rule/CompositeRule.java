package com.flipkart.masquerade.rule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shrey.garg on 24/05/17.
 */
public class CompositeRule extends ValueRule {
    private final List<ValueRule> valueRules;
    private final Conjunction conjunction;

    public CompositeRule(ValueRule... valueRules) {
        this(Conjunction.AND, valueRules);
    }

    public CompositeRule(Conjunction conjunction, ValueRule... valueRules) {
        if (conjunction == null || valueRules == null) {
            throw new NullPointerException("CompositeRule does not accept null values");
        }
        this.valueRules = Arrays.asList(valueRules);
        this.conjunction = conjunction;
    }

    public List<ValueRule> getValueRules() {
        return valueRules;
    }

    public Conjunction getConjunction() {
        return conjunction;
    }
}
