package com.flipkart.masquerade.test;

import com.flipkart.masquerade.test.actual.property.ChangeOverrideSetterAndGetter;
import com.flipkart.masquerade.test.actual.property.MultipleChange;
import com.flipkart.masquerade.test.actual.property.OneChange;
import com.flipkart.masquerade.test.actual.property.OneChangeOnGetter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by shrey.garg on 10/07/17.
 */
public class PropertyNameTest extends BaseTest {
    @Test
    public void testOneJsonProperty() throws Exception {
        OneChange oneChange = new OneChange();
        oneChange.setAbc("abc");
        oneChange.setAlways(true);
        oneChange.setDef("def");
        oneChange.setXyz(64);

        String serialized = cloak.hide(oneChange, defaultEval);

        assertEquals(mapper.writeValueAsString(oneChange), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testMultipleJsonProperty() throws Exception {
        MultipleChange multipleChange = new MultipleChange();
        multipleChange.setAbc("abc");
        multipleChange.setAlways(true);
        multipleChange.setDef("def");
        multipleChange.setXyz(64);

        String serialized = cloak.hide(multipleChange, defaultEval);

        assertEquals(mapper.writeValueAsString(multipleChange), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testOneJsonPropertyOnGetterAndSetter() throws Exception {
        OneChangeOnGetter oneChange = new OneChangeOnGetter();
        oneChange.setAbc("abc");
        oneChange.setAlways(true);
        oneChange.setDef("def");
        oneChange.setXyz(64);

        String serialized = cloak.hide(oneChange, defaultEval);

        assertEquals(mapper.writeValueAsString(oneChange), serialized);
        System.out.println(serialized);
    }

    @Test
    public void testChangeOverrideGetterAndSetter() throws Exception {
        ChangeOverrideSetterAndGetter changeOverrideSetterAndGetter = new ChangeOverrideSetterAndGetter();
        changeOverrideSetterAndGetter.setAbc("abc");
        changeOverrideSetterAndGetter.setAlways(true);
        changeOverrideSetterAndGetter.setDef("def");
        changeOverrideSetterAndGetter.setXyz(64);

        String serialized = cloak.hide(changeOverrideSetterAndGetter, defaultEval);

        assertEquals(mapper.writeValueAsString(changeOverrideSetterAndGetter), serialized);
        System.out.println(serialized);
    }
}
