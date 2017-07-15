package com.flipkart.masquerade.test.actual;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shrey.garg on 15/07/17.
 */
public class Others {
    private boolean is;
    @JsonProperty("sProductSuffix")
    private String productSuffix;
    private Integer a;
    private String isNotBoolean;

    public boolean isIs() {
        return is;
    }

    public void setIs(boolean is) {
        this.is = is;
    }

    public String getProductSuffix() {
        return productSuffix;
    }

    public void setProductSuffix(String productSuffix) {
        this.productSuffix = productSuffix;
    }

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public String getIsNotBoolean() {
        return isNotBoolean;
    }

    public void setIsNotBoolean(String isNotBoolean) {
        this.isNotBoolean = isNotBoolean;
    }
}
