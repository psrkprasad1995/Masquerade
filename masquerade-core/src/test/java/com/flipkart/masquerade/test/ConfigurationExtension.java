package com.flipkart.masquerade.test;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.rule.Operator;
import com.flipkart.masquerade.rule.Rule;
import com.flipkart.masquerade.rule.ValueRule;
import com.flipkart.masquerade.test.annotation.ConfigProvider;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by shrey.garg on 20/05/17.
 */
public class ConfigurationExtension implements ParameterResolver {

    @Override
    public boolean supports(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().isAnnotationPresent(ConfigProvider.class);
    }

    @Override
    public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getCloakPackage()).thenReturn("com.flipkart.masquerade.test");
        when(configuration.getPackagesToScan()).thenReturn(Arrays.asList("com.flipkart.test"));

        ValueRule valueRule = new ValueRule("a", Operator.LESSER, "getX()");

        Rule rule = new Rule(
                "TestOne",
                ValidationTest.class,
                Evaluator.class,
                Arrays.asList(valueRule)
        );

        Set<Rule> ruleSet = new HashSet<>(Arrays.asList(rule));
        when(configuration.getRules()).thenReturn(ruleSet);

        return configuration;
    }
}
