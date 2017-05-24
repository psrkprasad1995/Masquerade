package com.flipkart.masquerade;

import com.flipkart.masquerade.rule.Rule;

import java.util.List;
import java.util.Set;

/**
 * Created by shrey.garg on 25/04/17.
 */
public interface Configuration {
    List<String> getPackagesToScan();
    Set<Rule> getRules();
    String getCloakPackage();
}
