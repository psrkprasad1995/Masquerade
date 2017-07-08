package com.flipkart.masquerade.test.actual;

import com.flipkart.masquerade.test.Platform;
import com.flipkart.masquerade.test.ValidationAnnotation;

/**
 * Created by shrey.garg on 27/05/17.
 */
public class Four {
    private double aaDouble;
    @ValidationAnnotation(name = Platform.MOBILE_WEB, since = 42)
    private Double bbDouble;

    public Four() {
    }

    public Four(double aaDouble, Double bbDouble) {
        this.aaDouble = aaDouble;
        this.bbDouble = bbDouble;
    }

    public double getAaDouble() {
        return aaDouble;
    }

    public void setAaDouble(double aaDouble) {
        this.aaDouble = aaDouble;
    }

    public Double getBbDouble() {
        return bbDouble;
    }

    public void setBbDouble(Double bbDouble) {
        this.bbDouble = bbDouble;
    }
}
