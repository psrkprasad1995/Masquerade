package com.flipkart.masquerade.test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.test.veils.Cloak;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrey.garg on 09/07/17.
 */
public class BaseTest {
    protected static final ObjectMapper mapper = new ObjectMapper();
    protected List<String> missingClasses = new ArrayList<>();
    /* Pass missingClasses to the Cloak constructor when debug mode is on */
    protected final Cloak cloak = new Cloak();
    protected final Eval defaultEval = new Eval(Platform.ANDROID, 199);

    static {
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
}
