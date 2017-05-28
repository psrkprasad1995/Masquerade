package com.flipkart.masquerade.test;

import com.flipkart.masquerade.test.actual.One;
import com.flipkart.masquerade.test.actual.Two;
import com.flipkart.masquerade.test.actual.Wrapper;
import org.junit.jupiter.api.Test;
import org.test.veils.Cloak;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Created by shrey.garg on 28/05/17.
 */
public class SampleTest {
    @Test
    public void testSimpleCloaking() throws Exception {
        Cloak cloak = new Cloak();

        One one = new One();
        one.setT1("something");
        one.setT2(2);

        Eval eval = new Eval();
        eval.platform = Platform.ANDROID;
        eval.setClient("web");
        eval.setVersion(5);

        assertNotNull(one.getT2());
        cloak.hide(one, eval);
        assertNull(one.getT2());
    }

    @Test
    public void testSimpleNotCloaking() throws Exception {
        Cloak cloak = new Cloak();

        One one = new One();
        one.setT1("something");
        one.setT2(2);

        Eval eval = new Eval();
        eval.platform = Platform.MOBILE_WEB;
        eval.setClient("web");
        eval.setVersion(5);

        assertNotNull(one.getT2());
        cloak.hide(one, eval);
        assertNotNull(one.getT2());
    }

    @Test
    public void testGenericCloaking() throws Exception {
        Cloak cloak = new Cloak();

        One one = new One();
        one.setT1("something");
        one.setT2(2);

        Wrapper<One> wrapper = new Wrapper<>();
        wrapper.setResponse(one);

        Eval eval = new Eval();
        eval.platform = Platform.ANDROID;
        eval.setClient("web");
        eval.setVersion(5);

        assertNotNull(one.getT2());
        cloak.hide(wrapper, eval);
        assertNull(one.getT2());
    }

    @Test
    public void testComplexCloaking() throws Exception {
        Cloak cloak = new Cloak();

        One one = new One();
        one.setT1("something");
        one.setT2(2);

        Two two = new Two();
        two.setL1("else");
        two.setL2(7);
        two.setOne(one);

        one.setTwo(two);

        Eval eval = new Eval();
        eval.platform = Platform.ANDROID;
        eval.setClient("web");
        eval.setVersion(1);

        assertNotNull(one.getT2());
        assertNotNull(two.getL1());

        cloak.hide(one, eval);

        assertNull(one.getT2());
        assertNull(two.getL1());
    }
}
