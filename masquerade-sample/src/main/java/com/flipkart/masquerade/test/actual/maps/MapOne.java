package com.flipkart.masquerade.test.actual.maps;

import java.util.Map;

/**
 * Created by shrey.garg on 09/07/17.
 */
public class MapOne {
    private String abc;
    private boolean someBoolean;
    private Map<String, MapTwo> mapTwoMap;

    public String getAbc() {
        return abc;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }

    public boolean isSomeBoolean() {
        return someBoolean;
    }

    public void setSomeBoolean(boolean someBoolean) {
        this.someBoolean = someBoolean;
    }

    public Map<String, MapTwo> getMapTwoMap() {
        return mapTwoMap;
    }

    public void setMapTwoMap(Map<String, MapTwo> mapTwoMap) {
        this.mapTwoMap = mapTwoMap;
    }
}
