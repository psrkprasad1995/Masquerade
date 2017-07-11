package com.flipkart.masquerade.test.actual.inclusion;

import java.util.Map;

/**
 * Created by shrey.garg on 11/07/17.
 */
public class SubEmpty extends Empty {
    private Map<String, String> map;
    private Integer[] array;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Integer[] getArray() {
        return array;
    }

    public void setArray(Integer[] array) {
        this.array = array;
    }
}
