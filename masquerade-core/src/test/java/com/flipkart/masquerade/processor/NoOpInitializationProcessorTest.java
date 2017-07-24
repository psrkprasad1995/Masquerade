package com.flipkart.masquerade.processor;

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.processor.type.NoOpInitializationProcessor;
import com.flipkart.masquerade.rule.Rule;
import com.flipkart.masquerade.test.ConfigurationExtension;
import com.flipkart.masquerade.test.annotation.ConfigProvider;
import com.flipkart.masquerade.util.EntryType;
import com.flipkart.masquerade.util.RepositoryEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.flipkart.masquerade.util.Helper.getWrapperTypes;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by shrey.garg on 29/05/17.
 */
@ExtendWith(ConfigurationExtension.class)
public class NoOpInitializationProcessorTest {

    @Test
    public void generateNoOpEntries(@ConfigProvider Configuration configuration) {
        NoOpInitializationProcessor processor = new NoOpInitializationProcessor(configuration, null);

        for (Rule rule : configuration.getRules()) {
            List<RepositoryEntry> repositoryEntries = processor.generateNoOpEntries(rule);

            for (Class<?> clazz : getWrapperTypes()) {
                assertTrue(repositoryEntries.contains(new RepositoryEntry(rule, clazz, EntryType.NoOP)));
            }

            assertTrue(repositoryEntries.contains(new RepositoryEntry(rule, String.class, EntryType.NoOP)));
        }
    }

}