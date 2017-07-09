package com.flipkart.masquerade.test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.test.veils.Cloak;

/**
 * Created by shrey.garg on 09/07/17.
 */
public class BaseTest {
    protected static final ObjectMapper mapper = new ObjectMapper();
    protected final Cloak cloak = new Cloak();
    protected final Eval defaultEval = new Eval(Platform.ANDROID, 199);

    static {
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }
}
