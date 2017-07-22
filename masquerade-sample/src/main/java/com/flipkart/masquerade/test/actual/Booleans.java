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

package com.flipkart.masquerade.test.actual;

/**
 * Created by shrey.garg on 15/07/17.
 */
public class Booleans {
    private boolean isAbc;
    private boolean isis;
    private boolean isIs;
    private boolean a;

    public boolean isAbc() {
        return isAbc;
    }

    public void setAbc(boolean abc) {
        isAbc = abc;
    }

    public boolean isIsis() {
        return isis;
    }

    public void setIsis(boolean isis) {
        this.isis = isis;
    }

    public boolean isIs() {
        return isIs;
    }

    public void setIs(boolean is) {
        isIs = is;
    }

    public boolean isA() {
        return a;
    }

    public void setA(boolean a) {
        this.a = a;
    }
}
