package com.flipkart.masquerade.test.actual.inclusion;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by shrey.garg on 11/07/17.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Empty {
    private List<String> abc;
    private boolean something;
    private String def;

    public List<String> getAbc() {
        return abc;
    }

    public void setAbc(List<String> abc) {
        this.abc = abc;
    }

    public boolean isSomething() {
        return something;
    }

    public void setSomething(boolean something) {
        this.something = something;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }
}
