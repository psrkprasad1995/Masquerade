/*
 * Copyright 2017 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.masquerade.test;

import com.flipkart.masquerade.test.actual.*;
import com.flipkart.masquerade.test.actual.collections.CollectOne;
import com.flipkart.masquerade.test.actual.collections.CollectThree;
import com.flipkart.masquerade.test.actual.collections.CollectTwo;
import org.junit.jupiter.api.Test;
import org.test.veils.Cloak;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String serialized = cloak.hide(one, eval);
        assertNull(one.getT2());
        assertEquals("{\"t1\":\"something\",\"t2\":null,\"two\":null}", serialized);
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
        String serialized = cloak.hide(one, eval);
        assertNotNull(one.getT2());
        assertEquals("{\"t1\":\"something\",\"t2\":2,\"two\":null}", serialized);
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
        String serialized = cloak.hide(wrapper, eval);
        assertNull(one.getT2());
        assertEquals("{\"response\":{\"t1\":\"something\",\"t2\":null,\"two\":null}}", serialized);
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

        String serialized = cloak.hide(one, eval);

        assertNull(one.getT2());
        assertNull(two.getL1());
        assertEquals("{\"t1\":\"something\",\"t2\":null,\"two\":{\"l1\":null,\"l2\":7,\"three\":null,\"four\":null,\"primitiveBoolean\":false,\"wrapperBoolean\":null,\"fruit\":\"APPLE\"}}", serialized);
    }

    @Test
    public void testCollectionCloaking() throws Exception {
        Cloak cloak = new Cloak();

        CollectThree collectThree = new CollectThree();
        collectThree.setId(2);

        Three three1 = new Three(2, 53.125);
        Three three2 = new Three(624, 212.63);
        collectThree.setThrees(new Three[]{ three1, three2 });

        CollectOne collectOne = new CollectOne();
        collectOne.setCollectTwo(collectThree);

        Four four1 = new Four(1232.12324, 423.61);
        Four four2 = new Four(2643.12, 6943.255);
        Four four3 = new Four(4124.63, 90123.23);
        collectOne.setFours(Arrays.asList(four1, four2, four3));

        Eval eval = new Eval();
        eval.platform = Platform.MOBILE_WEB;
        eval.setVersion(1);

        assertNotNull(four1.getBbDouble());
        assertNotNull(four2.getBbDouble());
        assertNotNull(four3.getBbDouble());

        String serialized = cloak.hide(collectOne, eval);

        assertNull(four1.getBbDouble());
        assertNull(four2.getBbDouble());
        assertNull(four3.getBbDouble());

        assertEquals("{\"fours\":[{\"aaDouble\":1232.12324,\"bbDouble\":null},{\"aaDouble\":2643.12,\"bbDouble\":null},{\"aaDouble\":4124.63,\"bbDouble\":null}],\"collectTwo\":{\"threes\":[{\"a\":2,\"b\":53.125},{\"a\":624,\"b\":212.63}],\"id\":2}}", serialized);
    }
}
