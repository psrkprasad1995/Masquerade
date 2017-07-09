package com.flipkart.masquerade.test.actual.maps;

import com.flipkart.masquerade.test.Platform;
import com.flipkart.masquerade.test.ValidationAnnotation;

import java.util.Map;

/**
 * Created by shrey.garg on 09/07/17.
 */
public class MapTwo {
    @ValidationAnnotation(name = Platform.ANDROID, since = 65)
    private String tbm;
    private Map<String, Object> objectMap;

    public String getTbm() {
        return tbm;
    }

    public void setTbm(String tbm) {
        this.tbm = tbm;
    }

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

    public void setObjectMap(Map<String, Object> objectMap) {
        this.objectMap = objectMap;
    }
}
