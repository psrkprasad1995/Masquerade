package com.flipkart.masquerade.test;

import com.flipkart.masquerade.test.actual.Four;
import com.flipkart.masquerade.test.actual.debug.Unregistered;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by shrey.garg on 15/07/17.
 */
public class DebugModeTest extends BaseTest {
    /* Enable this test when debug mode is switched on */
//    @Test
//    public void testMissingClasses() throws Exception {
//        Unregistered unregistered = new Unregistered();
//        unregistered.setAbc("asb");
//        unregistered.setFour(new Four(123.214, 7423.1232124));
//        unregistered.setFruit(Fruit.ORANGE);
//        unregistered.setNotIncluded(new NotIncluded(true, "no"));
//
//        String serialized = cloak.hide(unregistered, defaultEval);
//        System.out.println(serialized);
//
//        assertEquals(1, missingClasses.size());
//        assertEquals(NotIncluded.class.getName(), missingClasses.get(0));
//    }
}
