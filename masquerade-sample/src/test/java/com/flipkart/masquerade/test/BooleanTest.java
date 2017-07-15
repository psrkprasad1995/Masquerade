package com.flipkart.masquerade.test;

import com.flipkart.masquerade.test.actual.BooleanWrappers;
import com.flipkart.masquerade.test.actual.Booleans;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by shrey.garg on 15/07/17.
 */
public class BooleanTest extends BaseTest {
    @Test
    public void testPrimitives() throws Exception {
        Booleans booleans = new Booleans();
        booleans.setAbc(true);
        booleans.setIs(true);
        booleans.setIsis(true);

        String serialized = cloak.hide(booleans, defaultEval);

        assertEquals(mapper.writeValueAsString(booleans), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testWrappers() throws Exception {
        BooleanWrappers booleans = new BooleanWrappers();
        booleans.setAbc(true);
        booleans.setIs(true);
        booleans.setIsis(true);

        String serialized = cloak.hide(booleans, defaultEval);

        assertEquals(mapper.writeValueAsString(booleans), serialized);
        System.out.println(serialized);
    }
}
