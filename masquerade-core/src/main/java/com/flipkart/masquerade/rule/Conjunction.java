package com.flipkart.masquerade.rule;

/**
 * Created by shrey.garg on 24/05/17.
 */
public enum Conjunction {
    AND("&&"),
    OR("||");

    private final String symbol;

    Conjunction(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
