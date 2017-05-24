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
