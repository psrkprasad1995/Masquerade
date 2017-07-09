package com.flipkart.masquerade.test;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.flipkart.masquerade.test.actual.order.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by shrey.garg on 09/07/17.
 */
@JsonPropertyOrder({ "always", "xyz", "def", "abc" })
public class PropertyOrderTest extends BaseTest {
    @Test
    public void testOnePropertyOrder() throws Exception {
        One one = new One();
        one.setAbc("abc");
        one.setAlways(true);
        one.setDef("def");
        one.setXyz(64);

        String serialized = cloak.hide(one, defaultEval);

        assertEquals(mapper.writeValueAsString(one), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testMultiplePropertyOrder() throws Exception {
        Two two = new Two();
        two.setAbc("abc");
        two.setAlways(true);
        two.setDef("def");
        two.setXyz(64);

        String serialized = cloak.hide(two, defaultEval);

        assertEquals(mapper.writeValueAsString(two), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testAllPropertyOrder() throws Exception {
        Three three = new Three();
        three.setAbc("abc");
        three.setAlways(true);
        three.setDef("def");
        three.setXyz(64);

        String serialized = cloak.hide(three, defaultEval);

        assertEquals(mapper.writeValueAsString(three), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testIncorrectPropertyOrder() throws Exception {
        Four four = new Four();
        four.setAbc("abc");
        four.setAlways(true);
        four.setDef("def");
        four.setXyz(64);

        String serialized = cloak.hide(four, defaultEval);

        assertEquals(mapper.writeValueAsString(four), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testIncorrectAndCorrectPropertyOrder() throws Exception {
        Five five = new Five();
        five.setAbc("abc");
        five.setAlways(true);
        five.setDef("def");
        five.setXyz(64);

        String serialized = cloak.hide(five, defaultEval);

        assertEquals(mapper.writeValueAsString(five), serialized);
        System.out.println(serialized);
    }
}
