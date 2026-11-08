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

public class EndianUtils_getIntBE_22_0_Test {

    @Test
    public void testGetIntBE() throws Exception {
        byte[] data = { 0x12, 0x34, 0x56, 0x78 };
        int expected = 0x12345678;
        Method method = EndianUtils.class.getDeclaredMethod("getIntBE", byte[].class);
        method.setAccessible(true);
        int result = (int) method.invoke(null, data);
        assertEquals(expected, result);
    }
}
