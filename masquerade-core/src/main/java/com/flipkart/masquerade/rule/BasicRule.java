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

/**
 * Created by shrey.garg on 24/04/17.
 */
public class BasicRule extends ValueRule {
    private final String annotationMember;
    private final Operator operator;
    private final String evaluatorFunction;
    private final boolean defaultIgnored;

    public BasicRule(String annotationMember, Operator operator, String evaluatorFunction) {
        this(annotationMember, operator, evaluatorFunction, true);
    }

    public BasicRule(String annotationMember, Operator operator, String evaluatorFunction, boolean defaultIgnored) {
        if (annotationMember == null || operator == null || evaluatorFunction == null) {
            throw new NullPointerException("BasicRule class does not accept any null parameters");
        }

        this.annotationMember = annotationMember;
        this.operator = operator;
        this.evaluatorFunction = evaluatorFunction;
        this.defaultIgnored = defaultIgnored;
    }

    public String getAnnotationMember() {
        return annotationMember;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getEvaluatorFunction() {
        return evaluatorFunction;
    }

    public boolean isDefaultIgnored() {
        return defaultIgnored;
    }
}
