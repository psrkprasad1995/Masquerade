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
import com.flipkart.masquerade.test.Platform;
import com.flipkart.masquerade.test.ValidationAnnotation;

/**
 * Created by shrey.garg on 11/07/17.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ClassDefault {
    private boolean def;
    @ValidationAnnotation(name = Platform.ANDROID, since = 12)
    private String abc;

    public boolean isDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
    }

    public String getAbc() {
        return abc;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }
}
