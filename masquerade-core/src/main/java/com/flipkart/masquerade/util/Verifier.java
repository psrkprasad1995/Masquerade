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

package com.flipkart.masquerade.util;

import com.flipkart.masquerade.rule.BasicRule;
import com.flipkart.masquerade.rule.CompositeRule;
import com.flipkart.masquerade.rule.Rule;
import com.flipkart.masquerade.rule.ValueRule;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static com.flipkart.masquerade.util.Helper.getWrapperTypes;

/**
 * Created by shrey.garg on 26/05/17.
 */
public class Verifier {
    public static void verifyEvaluationObject(Rule rule) {
        Class<?> evaluatorClass = rule.getEvaluatorClass();
        List<String> accessors = new ArrayList<>();
        getAccessors(rule.getValueRule(), accessors);
        for (String accessor : accessors) {
            try {
                if (accessor.endsWith("()")) {
                    evaluatorClass.getMethod(accessor.substring(0, accessor.length() - 2));
                } else {
                    evaluatorClass.getField(accessor);
                }
            } catch (NoSuchMethodException | NoSuchFieldException e) {
                throw new UnsupportedOperationException("Please specify a PUBLIC method or field for evaluator accessor");
            }
        }
    }

    public static void verifyAnnotation(Rule rule) {
        Class<? extends Annotation> annotationClass = rule.getAnnotationClass();
        List<String> members = new ArrayList<>();
        getMembers(rule.getValueRule(), members);
        for (String member : members) {
            try {
                annotationClass.getDeclaredMethod(member);
            } catch (NoSuchMethodException e) {
                throw new UnsupportedOperationException("Please specify a method that belongs to the Annotation");
            }
        }
    }

    public static void verifyTypes(Rule rule) {
        verifyValueRuleTypes(rule.getValueRule(), rule);
    }

    private static void verifyValueRuleTypes(ValueRule valueRule, Rule rule) {
        if (valueRule instanceof CompositeRule) {
            for (ValueRule subRule : ((CompositeRule) valueRule).getValueRules()) {
                verifyValueRuleTypes(subRule, rule);
            }
        } else {
            BasicRule basicRule = (BasicRule) valueRule;
            Class<?> evaluatorClass = rule.getEvaluatorClass();
            Class<? extends Annotation> annotationClass = rule.getAnnotationClass();

            try {
                Class<?> evaluatorReturnType;
                String accessor = basicRule.getEvaluatorFunction();
                if (accessor.endsWith("()")) {
                    evaluatorReturnType = evaluatorClass.getMethod(accessor.substring(0, accessor.length() - 2)).getReturnType();
                } else {
                    evaluatorReturnType = evaluatorClass.getField(accessor).getType();
                }

                Class<?> annotationMemberReturnType = annotationClass.getDeclaredMethod(basicRule.getAnnotationMember()).getReturnType();

                if (!evaluatorReturnType.getName().equals(annotationMemberReturnType.getName())) {
                    throw new UnsupportedOperationException("Accessors and Annotation members should be compatible");
                }
            } catch (NoSuchMethodException | NoSuchFieldException e) {
                throw new UnsupportedOperationException("Please specify a PUBLIC method or field for evaluator accessor");
            }
        }
    }

    private static void getMembers(ValueRule valueRule, List<String> members) {
        if (valueRule instanceof CompositeRule) {
            for (ValueRule subRule : ((CompositeRule) valueRule).getValueRules()) {
                getMembers(subRule, members);
            }
        } else {
            BasicRule basicRule = (BasicRule) valueRule;
            members.add(basicRule.getAnnotationMember());
        }
    }

    private static void getAccessors(ValueRule valueRule, List<String> accessors) {
        if (valueRule instanceof CompositeRule) {
            for (ValueRule subRule : ((CompositeRule) valueRule).getValueRules()) {
                getAccessors(subRule, accessors);
            }
        } else {
            BasicRule basicRule = (BasicRule) valueRule;
            accessors.add(basicRule.getEvaluatorFunction());
        }
    }
}
