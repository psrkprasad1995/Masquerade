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

package test.actual;

import test.Platform;
import test.ValidationAnnotation;

/**
 * Created by shrey.garg on 24/04/17.
 */
public class One {
    private String t1;
    @ValidationAnnotation(name = Platform.ANDROID, since = 9)
    @ValidationAnnotation(name = Platform.iOS, since = 4)
    private Integer t2;

    public String getT1() {
        return t1;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }

    public Integer getT2() {
        return t2;
    }

    public void setT2(Integer t2) {
        this.t2 = t2;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("One{");
        sb.append("t1='").append(t1).append('\'');
        sb.append(", t2=").append(t2);
        sb.append('}');
        return sb.toString();
    }
}
