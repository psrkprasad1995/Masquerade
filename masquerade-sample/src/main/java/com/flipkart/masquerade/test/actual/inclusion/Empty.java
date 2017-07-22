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

package com.flipkart.masquerade.test.actual.inclusion;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by shrey.garg on 11/07/17.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Empty {
    private List<String> abc;
    private boolean something;
    private String def;

    public List<String> getAbc() {
        return abc;
    }

    public void setAbc(List<String> abc) {
        this.abc = abc;
    }

    public boolean isSomething() {
        return something;
    }

    public void setSomething(boolean something) {
        this.something = something;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }
}
