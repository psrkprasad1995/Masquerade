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

import com.flipkart.masquerade.Configuration;
import com.flipkart.masquerade.rule.BasicRule;
import com.flipkart.masquerade.rule.Rule;
import com.google.common.reflect.ClassPath;
import com.squareup.javapoet.ClassName;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static com.flipkart.masquerade.util.Strings.*;

/**
 * Created by shrey.garg on 25/04/17.
 */
public class Helper {
    public static String getSetterName(String name) {
        String capitalizedName = capitalize(name);
        String prefix = "set";
        return prefix + capitalizedName;
    }

    public static String getGetterName(String name, boolean isBoolean, boolean isPrimitive) {
        String capitalizedName = capitalize(name);
        String prefix = "get";

        if (isBoolean) {
            if (capitalizedName.startsWith("Is")) {
                if (capitalizedName.length() > 2 && Character.isUpperCase(capitalizedName.charAt(2))) {
                    capitalizedName = capitalizedName.substring(2);
                }
            }

            if (isPrimitive) {
                prefix = "is";
            }
        }

        return prefix + capitalizedName;
    }

    public static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }

    private static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static ClassName getRuleInterface(Configuration configuration, Rule rule) {
        return ClassName.get(configuration.getCloakPackage(), rule.getName() + INTERFACE_SUFFIX);
    }

    public static String getInterfaceName(Rule rule) {
        return rule.getName() + INTERFACE_SUFFIX;
    }

    public static String getImplementationName(Rule rule, Class<?> clazz) {
        return generateImplementationName(rule, clazz.getSimpleName());
    }

    public static String getNoOpImplementationName(Rule rule) {
        return generateImplementationName(rule, "NoOp");
    }

    public static String getEnumImplementationName(Rule rule) {
        return generateImplementationName(rule, "Enum");
    }

    public static ClassName getNoOpImplementationClass(Configuration configuration, Rule rule) {
        return ClassName.get(configuration.getCloakPackage(), getNoOpImplementationName(rule));
    }

    public static ClassName getEnumImplementationClass(Configuration configuration, Rule rule) {
        return ClassName.get(configuration.getCloakPackage(), getEnumImplementationName(rule));
    }

    private static String generateImplementationName(Rule rule, String prefix) {
        return prefix + rule.getName() + INTERFACE_SUFFIX;
    }

    public static String getImplementationPackage(Configuration configuration, Class<?> clazz) {
        return configuration.getCloakPackage() + "." + clazz.getPackage().getName();
    }

    public static ClassName getEntryClass(Configuration configuration) {
        return ClassName.get(configuration.getCloakPackage(), ENTRY_CLASS);
    }

    public static String getNoOpVariableName(Rule rule) {
        return "noOp" + rule.getName();
    }

    public static String getEnumVariableName(Rule rule) {
        return "enum" + rule.getName();
    }

    public static Set<ClassPath.ClassInfo> getPackageClasses(ClassLoader classLoader, List<String> packagesToScan) throws IOException {
        ClassPath classpath = ClassPath.from(classLoader);
        Set<ClassPath.ClassInfo> classDescriptions = new HashSet<>();
        for (String basePackage : packagesToScan) {
            classDescriptions.addAll(classpath.getTopLevelClassesRecursive(basePackage));
        }
        return classDescriptions;
    }

    public static List<Field> getNonStaticFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(
                    Arrays.asList(c.getDeclaredFields()).stream()
                            .filter(field -> !Modifier.isStatic(field.getModifiers()))
                            .collect(Collectors.toList())
            );
        }
        return fields;
    }

    public static String getEvaluationFunction(BasicRule basicRule) {
        return EVAL_PARAMETER + "." + basicRule.getEvaluatorFunction();
    }

    public static boolean isAbstract(Class clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static boolean isPublic(Class clazz) {
        return Modifier.isPublic(clazz.getModifiers());
    }
}
