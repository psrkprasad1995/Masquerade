package com.flipkart.masquerade.test;

/**
 * Created by shrey.garg on 15/07/17.
 */
public class NotIncluded {
    private boolean success;
    private String failure;

    public NotIncluded() {
    }

    public NotIncluded(boolean success, String failure) {
        this.success = success;
        this.failure = failure;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }
}
