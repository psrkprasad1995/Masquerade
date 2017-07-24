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

package com.flipkart.masquerade.test.cloaking.actual;

import com.flipkart.masquerade.test.cloaking.Platform;
import com.flipkart.masquerade.test.cloaking.ValidationAnnotation;

/**
 * Created by shrey.garg on 27/05/17.
 */
public class Four {
    private double aaDouble;
    @ValidationAnnotation(name = Platform.MOBILE_WEB, since = 42)
    private Double bbDouble;

    public Four() {
    }

    public Four(double aaDouble, Double bbDouble) {
        this.aaDouble = aaDouble;
        this.bbDouble = bbDouble;
    }

    public double getAaDouble() {
        return aaDouble;
    }

    public void setAaDouble(double aaDouble) {
        this.aaDouble = aaDouble;
    }

    public Double getBbDouble() {
        return bbDouble;
    }

    public void setBbDouble(Double bbDouble) {
        this.bbDouble = bbDouble;
    }
}
