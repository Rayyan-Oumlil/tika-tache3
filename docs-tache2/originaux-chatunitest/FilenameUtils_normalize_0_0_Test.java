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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.*;
import org.mockito.*;

public class FilenameUtils_normalize_0_0_Test {

    private Method normalizeMethod;

    @BeforeEach
    public void setUp() throws NoSuchMethodException {
        normalizeMethod = FilenameUtils.class.getDeclaredMethod("normalize", String.class);
        normalizeMethod.setAccessible(true);
    }

    @Test
    public void testNormalize_NullInput_ThrowsIllegalArgumentException() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            normalizeMethod.invoke(null, (Object) null);
        });
    }

    @Test
    public void testNormalize_EmptyString_ReturnsEmptyString() throws Exception {
        assertEquals("", normalizeMethod.invoke(null, ""));
    }

    @Test
    public void testNormalize_ReservedCharacter_ReplacesWithHex() throws Exception {
        String input = "test*file.txt";
        String expectedOutput = "test%2Afile.txt";
        assertEquals(expectedOutput, normalizeMethod.invoke(null, input));
    }

    @Test
    public void testNormalize_NoReservedCharacters_ReturnsOriginalString() throws Exception {
        String input = "example_file.txt";
        String expectedOutput = "example_file.txt";
        assertEquals(expectedOutput, normalizeMethod.invoke(null, input));
    }
}
