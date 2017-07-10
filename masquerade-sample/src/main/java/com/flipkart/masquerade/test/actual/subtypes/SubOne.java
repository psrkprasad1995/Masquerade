package com.flipkart.masquerade.test.actual.subtypes;

import com.flipkart.masquerade.test.actual.Four;

/**
 * Created by shrey.garg on 10/07/17.
 */
public class SubOne extends Base {
    private Four four;

    public Four getFour() {
        return four;
    }

    public void setFour(Four four) {
        this.four = four;
    }
}
