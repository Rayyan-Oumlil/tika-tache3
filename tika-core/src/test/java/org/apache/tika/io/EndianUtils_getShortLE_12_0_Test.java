/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class EndianUtils_getShortLE_12_0_Test {

    @Test
    public void testGetShortLE() throws Exception {
        // Create an instance of the focal class using reflection
        Class<?> clazz = EndianUtils.class;
        Method getShortLEMethod = clazz.getDeclaredMethod("getShortLE", byte[].class, int.class);
        getShortLEMethod.setAccessible(true);
        // Test data
        byte[] testData = new byte[] { 0x12, 0x34 };
        int offset = 0;
        // Invoke the focal method using reflection
        short result = (short) getShortLEMethod.invoke(null, testData, offset);
        // Verify the result
        assertEquals(0x3412, result);
    }
}
