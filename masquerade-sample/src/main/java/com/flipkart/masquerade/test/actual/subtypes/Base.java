package com.flipkart.masquerade.test.actual.subtypes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by shrey.garg on 10/07/17.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SubOne.class, name = "SUB_ONE"),
        @JsonSubTypes.Type(value = SubTwo.class, name = "SUB_TWO")
})
public abstract class Base {
    private String abc;
    private String def;

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
}
