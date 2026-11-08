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

public class EndianUtils_getUIntLE_24_2_Test {

    @Test
    public void testGetUIntLE() throws Exception {
        // Create an instance of the focal class
        EndianUtils endianUtils = new EndianUtils();
        // Get the private method using reflection
        Method getUIntLEMethod = EndianUtils.class.getDeclaredMethod("getUIntLE", byte[].class, int.class);
        getUIntLEMethod.setAccessible(true);
        // Test cases
        byte[] data1 = { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        long result1 = (long) getUIntLEMethod.invoke(endianUtils, data1, 0);
        assertEquals(1, result1, "Test case 1 failed");
        byte[] data2 = { 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        long result2 = (long) getUIntLEMethod.invoke(endianUtils, data2, 4);
        assertEquals(4294967295L, result2, "Test case 2 failed");
        byte[] data3 = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
        long result3 = (long) getUIntLEMethod.invoke(endianUtils, data3, 0);
        assertEquals(0x04030201L, result3, "Test case 3 failed");
    }
}
