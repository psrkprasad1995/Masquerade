package com.flipkart.masquerade.test;

import com.flipkart.masquerade.test.actual.ignore.AllIgnore;
import com.flipkart.masquerade.test.actual.ignore.MultipleIgnore;
import com.flipkart.masquerade.test.actual.ignore.NoIgnore;
import com.flipkart.masquerade.test.actual.ignore.OneIgnore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by shrey.garg on 10/07/17.
 */
public class IgnoreTest extends BaseTest {
    @Test
    public void testNoJsonIgnoreFields() throws Exception {
        NoIgnore noIgnore = new NoIgnore();
        noIgnore.setAbc("abc");
        noIgnore.setAlways(true);
        noIgnore.setDef("def");
        noIgnore.setXyz(64);

        String serialized = cloak.hide(noIgnore, defaultEval);

        assertEquals(mapper.writeValueAsString(noIgnore), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testOneJsonIgnoreField() throws Exception {
        OneIgnore oneIgnore = new OneIgnore();
        oneIgnore.setAbc("abc");
        oneIgnore.setAlways(true);
        oneIgnore.setDef("def");
        oneIgnore.setXyz(64);

        String serialized = cloak.hide(oneIgnore, defaultEval);

        assertEquals(mapper.writeValueAsString(oneIgnore), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testMultipleJsonIgnoreFields() throws Exception {
        MultipleIgnore multipleIgnore = new MultipleIgnore();
        multipleIgnore.setAbc("abc");
        multipleIgnore.setAlways(true);
        multipleIgnore.setDef("def");
        multipleIgnore.setXyz(64);

        String serialized = cloak.hide(multipleIgnore, defaultEval);

        assertEquals(mapper.writeValueAsString(multipleIgnore), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testAllJsonIgnoreFields() throws Exception {
        AllIgnore allIgnore = new AllIgnore();
        allIgnore.setAbc("abc");
        allIgnore.setAlways(true);
        allIgnore.setDef("def");
        allIgnore.setXyz(64);

        String serialized = cloak.hide(allIgnore, defaultEval);

        assertEquals(mapper.writeValueAsString(allIgnore), serialized);
        System.out.println(serialized);
    }
}
