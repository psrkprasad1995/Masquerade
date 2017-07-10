package com.flipkart.masquerade.test;

import com.flipkart.masquerade.test.actual.Four;
import com.flipkart.masquerade.test.actual.Three;
import com.flipkart.masquerade.test.actual.subtypes.SubOne;
import com.flipkart.masquerade.test.actual.subtypes.SubTwo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by shrey.garg on 10/07/17.
 */
public class SubTypesTest extends BaseTest {
    @Test
    public void testSubTypes() throws Exception {
        SubOne subOne = new SubOne();
        subOne.setAbc("abc");
        subOne.setDef("def");
        subOne.setFour(new Four(213.632, 42.12342));

        String serialized = cloak.hide(subOne, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(subOne), serialized);

        SubTwo subTwo = new SubTwo();
        subTwo.setAbc("abc");
        subTwo.setDef("def");
        subTwo.setThree(new Three(764323, 42.12342));

        String serializedTwo = cloak.hide(subTwo, defaultEval);
        System.out.println(serializedTwo);

        assertEquals(mapper.writeValueAsString(subTwo), serializedTwo);
    }
}
