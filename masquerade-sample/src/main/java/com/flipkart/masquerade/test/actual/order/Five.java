package com.flipkart.masquerade.test.actual.order;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by shrey.garg on 09/07/17.
 */
@JsonPropertyOrder({ "random", "def" })
public class Five {
    private String abc;
    private String def;
    private int xyz;
    private boolean always;

    public String getAbc() {
        return abc;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public int getXyz() {
        return xyz;
    }

    public void setXyz(int xyz) {
        this.xyz = xyz;
    }

    public boolean isAlways() {
        return always;
    }

    public void setAlways(boolean always) {
        this.always = always;
    }

}
