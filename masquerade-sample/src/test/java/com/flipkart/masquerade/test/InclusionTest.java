package com.flipkart.masquerade.test;

import com.flipkart.masquerade.test.actual.Four;
import com.flipkart.masquerade.test.actual.Three;
import com.flipkart.masquerade.test.actual.inclusion.ClassDefault;
import com.flipkart.masquerade.test.actual.inclusion.Empty;
import com.flipkart.masquerade.test.actual.inclusion.OneDefault;
import com.flipkart.masquerade.test.actual.inclusion.SubEmpty;
import com.flipkart.masquerade.test.actual.subtypes.SubOne;
import com.flipkart.masquerade.test.actual.subtypes.SubTwo;
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

        String serialized = cloak.hide(empty, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(empty), serialized);
    }

    @Test
    public void testSubClassNonEmptyWithValue() throws Exception {
        SubEmpty empty = new SubEmpty();
        empty.setAbc(Arrays.asList("a", "d"));
        empty.setMap(Collections.singletonMap("xa", "ax"));

        String serialized = cloak.hide(empty, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(empty), serialized);
    }
}
