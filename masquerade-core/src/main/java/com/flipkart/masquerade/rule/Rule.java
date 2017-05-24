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

package com.flipkart.masquerade.rule;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by shrey.garg on 24/04/17.
 */
public class Rule {
    private final String name;
    private final Class<? extends Annotation> annotationClass;
    private final Class<?> evaluatorClass;
    private final List<ValueRule> valueRules;

    public Rule(String name, Class<? extends Annotation> annotationClass, Class<?> evaluatorClass, List<ValueRule> valueRules) {
        if (name == null || annotationClass == null || evaluatorClass == null || valueRules == null) {
            throw new NullPointerException("Rule class does not accept any null parameters");
        }

        this.name = name;
        this.annotationClass = annotationClass;
        this.evaluatorClass = evaluatorClass;
        this.valueRules = valueRules;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public Class<?> getEvaluatorClass() {
        return evaluatorClass;
    }

    public List<ValueRule> getValueRules() {
        return valueRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rule)) return false;

        Rule rule = (Rule) o;

        return name.equals(rule.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
