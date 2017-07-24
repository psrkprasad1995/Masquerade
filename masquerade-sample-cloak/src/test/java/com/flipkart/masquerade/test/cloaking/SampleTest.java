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

package com.flipkart.masquerade.test.cloaking;

import com.flipkart.masquerade.test.cloaking.actual.*;
import com.flipkart.masquerade.test.cloaking.actual.collections.CollectOne;
import com.flipkart.masquerade.test.cloaking.actual.collections.CollectThree;
import com.flipkart.masquerade.test.cloaking.actual.maps.MapOne;
import com.flipkart.masquerade.test.cloaking.actual.maps.MapTwo;
import org.junit.jupiter.api.Test;
import org.test.veils.Cloak;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Created by shrey.garg on 25/07/17.
 */
public class SampleTest {
    protected final Cloak cloak = new Cloak();

    @Test
    public void testSimpleCloaking() throws Exception {
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

    @Test
    public void testCollectionCloaking() throws Exception {
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

        cloak.hide(collectOne, eval);

        assertNull(four1.getBbDouble());
        assertNull(four2.getBbDouble());
        assertNull(four3.getBbDouble());
    }

    @Test
    public void testMapCloaking() throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("b", 43.63);
        objectMap.put("a", false);
        objectMap.put("d", "Jack");
        objectMap.put("c", new Three(4, 621.1));

        MapTwo mapTwo = new MapTwo();
        mapTwo.setTbm("Jill");
        mapTwo.setObjectMap(objectMap);

        MapTwo mapTwo1 = new MapTwo();
        mapTwo1.setTbm("Went");
        mapTwo1.setObjectMap(new HashMap<>());

        Map<String, MapTwo> mapTwoMap = new HashMap<>();
        mapTwoMap.put("1", mapTwo);
        mapTwoMap.put("2", mapTwo1);

        MapOne mapOne = new MapOne();
        mapOne.setAbc("xyz");
        mapOne.setSomeBoolean(true);
        mapOne.setMapTwoMap(mapTwoMap);

        Eval eval = new Eval();
        eval.platform = Platform.ANDROID;
        eval.setVersion(12);

        assertNotNull(mapTwo.getTbm());
        assertNotNull(mapTwo1.getTbm());

        cloak.hide(mapOne, eval);

        assertNull(mapTwo.getTbm());
        assertNull(mapTwo1.getTbm());
    }
}
