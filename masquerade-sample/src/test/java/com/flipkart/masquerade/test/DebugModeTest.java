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

import com.flipkart.masquerade.test.actual.Four;
import com.flipkart.masquerade.test.actual.debug.Unregistered;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
//        Set<String> missingClasses = cloak.getMissingClasses();
//        assertEquals(1, missingClasses.size());
//        assertEquals(NotIncluded.class.getName(), missingClasses.toArray()[0]);
//    }
}
