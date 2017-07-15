package com.flipkart.masquerade.test.actual.debug;

import com.flipkart.masquerade.test.Fruit;
import com.flipkart.masquerade.test.NotIncluded;
import com.flipkart.masquerade.test.actual.Four;
import com.flipkart.masquerade.util.Strings;

/**
 * Created by shrey.garg on 15/07/17.
 */
public class Unregistered {
    private Four four;
    private String abc;
    private Fruit fruit;
    private NotIncluded notIncluded;

    public Four getFour() {
        return four;
    }

    public void setFour(Four four) {
        this.four = four;
    }

    public String getAbc() {
        return abc;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }

    public Fruit getFruit() {
        return fruit;
    }

    public void setFruit(Fruit fruit) {
        this.fruit = fruit;
    }

    public NotIncluded getNotIncluded() {
        return notIncluded;
    }

    public void setNotIncluded(NotIncluded notIncluded) {
        this.notIncluded = notIncluded;
    }
}
