package com.flipkart.masquerade.test;

import com.flipkart.masquerade.Masquerade;

import java.io.File;
import java.io.IOException;

/**
 * Created by shrey.garg on 09/07/17.
 */
public class Gen {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Masquerade.initialize(new TestConfig(), new File("/Volumes/FourthImage/Masquerade/masquerade-sample/src/main/java"));
    }
}
