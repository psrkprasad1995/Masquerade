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

import com.flipkart.masquerade.test.actual.inclusion.ClassDefault;
import com.flipkart.masquerade.test.actual.inclusion.Empty;
import com.flipkart.masquerade.test.actual.inclusion.OneDefault;
import com.flipkart.masquerade.test.actual.inclusion.SubEmpty;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by shrey.garg on 10/07/17.
 */
public class InclusionTest extends BaseTest {
    @Test
    public void testOneDefaultInclusion() throws Exception {
        OneDefault oneDefault = new OneDefault();
        oneDefault.setAbc("abc");

        String serialized = cloak.hide(oneDefault, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(oneDefault), serialized);
    }

    @Test
    public void testOneNullInclusion() throws Exception {
        OneDefault oneDefault = new OneDefault();
        oneDefault.setDef(true);

        String serialized = cloak.hide(oneDefault, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(oneDefault), serialized);
    }

    @Test
    public void testOneNullInclusionWithCloakHidden() throws Exception {
        OneDefault oneDefault = new OneDefault();
        oneDefault.setAbc("abn");
        oneDefault.setDef(true);

        String serialized = cloak.hide(oneDefault, new Eval(Platform.ANDROID, 1));
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(oneDefault), serialized);
    }

    @Test
    public void testEmptyBeanInclusionWithCloakHidden() throws Exception {
        OneDefault oneDefault = new OneDefault();

        String serialized = cloak.hide(oneDefault, new Eval(Platform.ANDROID, 1));
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(oneDefault), serialized);
    }

    @Test
    public void testClassDefaultInclusion() throws Exception {
        ClassDefault classDefault = new ClassDefault();
        classDefault.setAbc("abc");

        String serialized = cloak.hide(classDefault, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(classDefault), serialized);
    }

    @Test
    public void testClassDefaultInclusionWithNullable() throws Exception {
        ClassDefault classDefault = new ClassDefault();
        classDefault.setDef(true);

        String serialized = cloak.hide(classDefault, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(classDefault), serialized);
    }

    @Test
    public void testClassDefaultInclusionWithNullableAndCloakHidden() throws Exception {
        OneDefault oneDefault = new OneDefault();
        oneDefault.setAbc("abn");
        oneDefault.setDef(true);

        String serialized = cloak.hide(oneDefault, new Eval(Platform.ANDROID, 1));
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(oneDefault), serialized);
    }

    @Test
    public void testClassNonEmptyWithNull() throws Exception {
        Empty empty = new Empty();

        String serialized = cloak.hide(empty, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(empty), serialized);
    }

    @Test
    public void testClassNonEmptyWithEmpty() throws Exception {
        Empty empty = new Empty();
        empty.setAbc(new ArrayList<>());

        String serialized = cloak.hide(empty, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(empty), serialized);
    }

    @Test
    public void testClassNonEmptyWithValue() throws Exception {
        Empty empty = new Empty();
        empty.setAbc(Arrays.asList("a", "d"));

        String serialized = cloak.hide(empty, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(empty), serialized);
    }

    @Test
    public void testClassNonEmptyWithEmptyString() throws Exception {
        Empty empty = new Empty();
        empty.setAbc(Arrays.asList("a", "d"));
        empty.setDef("");

        String serialized = cloak.hide(empty, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(empty), serialized);
    }

    @Test
    public void testClassNonEmptyWithBlankString() throws Exception {
        Empty empty = new Empty();
        empty.setAbc(Arrays.asList("a", "d"));
        empty.setDef(" ");

        String serialized = cloak.hide(empty, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(empty), serialized);
    }

    @Test
    public void testSubClassNonEmptyWithEmpty() throws Exception {
        SubEmpty empty = new SubEmpty();
        empty.setAbc(new ArrayList<>());
        empty.setMap(new HashMap<>());
        empty.setArray(new Integer[] {});

        String serialized = cloak.hide(empty, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(empty), serialized);
    }

    @Test
    public void testSubClassNonEmptyWithValue() throws Exception {
        SubEmpty empty = new SubEmpty();
        empty.setAbc(Arrays.asList("a", "d"));
        empty.setMap(Collections.singletonMap("xa", "ax"));
        empty.setArray(new Integer[] {1, 2, 5});

        String serialized = cloak.hide(empty, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(empty), serialized);
    }
}
