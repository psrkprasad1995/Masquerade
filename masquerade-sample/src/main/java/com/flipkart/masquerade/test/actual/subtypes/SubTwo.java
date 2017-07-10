package com.flipkart.masquerade.test.actual.subtypes;

import com.flipkart.masquerade.test.actual.Three;

/**
 * Created by shrey.garg on 10/07/17.
 */
public class SubTwo extends Base {
    private Three three;

    public Three getThree() {
        return three;
    }

    public void setThree(Three three) {
        this.three = three;
    }
}
