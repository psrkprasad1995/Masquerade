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

package com.flipkart.masquerade.test.cloaking.actual.collections;

import com.flipkart.masquerade.test.cloaking.actual.Four;

import java.util.List;

/**
 * Created by shrey.garg on 09/07/17.
 */
public class CollectOne {
    private List<Four> fours;
    private CollectTwo collectTwo;

    public List<Four> getFours() {
        return fours;
    }

    public void setFours(List<Four> fours) {
        this.fours = fours;
    }

    public CollectTwo getCollectTwo() {
        return collectTwo;
    }

    public void setCollectTwo(CollectTwo collectTwo) {
        this.collectTwo = collectTwo;
    }
}
