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
