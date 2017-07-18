package com.flipkart.masquerade.test.actual;

import com.flipkart.masquerade.test.NotIncluded;

import java.util.List;

/**
 * Created by shrey.garg on 18/07/17.
 */
public class Fallback {
    private NotIncluded notIncluded;
    private List<Object> objects;
    private String string;

    public NotIncluded getNotIncluded() {
        return notIncluded;
    }

    public void setNotIncluded(NotIncluded notIncluded) {
        this.notIncluded = notIncluded;
    }

    public List<Object> getObjects() {
        return objects;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
