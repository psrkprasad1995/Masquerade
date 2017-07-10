package com.flipkart.masquerade.test.actual.ignore;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by shrey.garg on 10/07/17.
 */
public class OneIgnore {
    private String abc;
    @JsonIgnore
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
