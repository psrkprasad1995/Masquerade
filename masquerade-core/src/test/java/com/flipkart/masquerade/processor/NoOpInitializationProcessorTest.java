package com.flipkart.masquerade.processor;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.processor.type.NoOpInitializationProcessor;
import com.flipkart.masquerade.rule.Rule;
import com.flipkart.masquerade.test.ConfigurationExtension;
import com.flipkart.masquerade.test.annotation.ConfigProvider;
import com.squareup.javapoet.CodeBlock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.flipkart.masquerade.util.Helper.getWrapperTypes;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by shrey.garg on 29/05/17.
 */
@ExtendWith(ConfigurationExtension.class)
public class NoOpInitializationProcessorTest {

    @Test
    public void generateNoOpEntries(@ConfigProvider Configuration configuration) {
        NoOpInitializationProcessor processor = new NoOpInitializationProcessor(configuration, null);

        for (Rule rule : configuration.getRules()) {
            CodeBlock.Builder codeBlock = CodeBlock.builder();
            processor.generateNoOpEntries(rule, codeBlock);
            CodeBlock build = codeBlock.build();

            for (Class<?> clazz : getWrapperTypes()) {
                assertTrue(build.toString().contains(String.format("%s.put(\"%s\", noOp%s);", rule.getName(), clazz.getName(), rule.getName())));
            }

            assertTrue(build.toString().contains(String.format("%s.put(\"%s\", noOp%s);", rule.getName(), String.class.getName(), rule.getName())));
        }
    }

}