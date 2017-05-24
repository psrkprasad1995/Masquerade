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

package test;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.rule.Operator;
import com.flipkart.masquerade.rule.Rule;
import com.flipkart.masquerade.rule.ValueRule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shrey.garg on 25/04/17.
 */
public class TestConfig implements Configuration {
    private static Set<Rule> rules = new HashSet<>();

    static {
        ValueRule valueRuleOne = new ValueRule("since", Operator.GREATER_EQUAL, "getVersion()");
        ValueRule valueRuleThree = new ValueRule("till", Operator.LESSER_EQUAL, "getVersion()");
        ValueRule valueRuleTwo = new ValueRule("name", Operator.EQUAL, "getPlatform()");
        List<ValueRule> valueRules = Arrays.asList(valueRuleTwo, valueRuleOne, valueRuleThree);

        Rule rule = new Rule("VaAn", ValidationAnnotation.class, Eval.class, valueRules);
        rules.add(rule);
    }

    @Override
    public List<String> getPackagesToScan() {
        return Arrays.asList("test.actual");
    }

    @Override
    public Set<Rule> getRules() {
        return rules;
    }

    @Override
    public String getCloakPackage() {
        return "org.test.veils";
    }
}
