package com.flipkart.masquerade.test;

import com.flipkart.masquerade.test.actual.Others;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by shrey.garg on 15/07/17.
 */
public class IntegrationTest extends BaseTest {
    @Test
    public void test() throws Exception {
        Others others = new Others();
        others.setA(32);
        others.setIs(true);
        others.setProductSuffix("something");
        others.setIsNotBoolean("not a boolean");
        others.setArrayList(new ArrayList<>());
        others.setUri(URI.create("/3/product/reviews?start=0&count=3&productId=SGLEHCBEZVHH5BBF&sortOrder=MOST_HELPFUL&ratings=ALL&reviewType=ALL&reviewerType=ALL&infiniteScroll=false"));
        others.setInts(new int[] { 1, 2, 3 });
        others.setChars(new char[] { 'a', 'b', 'c' });

        String serialized = cloak.hide(others, defaultEval);
        System.out.println(serialized);

        assertEquals(mapper.writeValueAsString(others), serialized);
    }
}
