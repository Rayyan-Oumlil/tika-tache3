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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import org.apache.tika.metadata.Metadata;

public class FilenameUtils_calculateExtension_10_1_Test {

    private Metadata metadata;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        metadata = new Metadata();
    }

    @Test
    public void testCalculateExtension_withContentType_png() throws Exception {
        metadata.set(Metadata.CONTENT_TYPE, "image/png");
        String result = FilenameUtils.calculateExtension(metadata, ".bin");
        assertEquals(".png", result);
    }

    @Test
    public void testCalculateExtension_withContentType_ocr_png() throws Exception {
        metadata.set(Metadata.CONTENT_TYPE, "image/ocr-png");
        String result = FilenameUtils.calculateExtension(metadata, ".bin");
        assertEquals(".png", result);
    }

    @Test
    public void testCalculateExtension_withContentType_null() throws Exception {

        String result = FilenameUtils.calculateExtension(metadata, ".bin");
        assertEquals(".bin", result);
    }

    private Object invokePrivateMethod(Object obj, String methodName, Object... args) throws Exception {
        java.lang.reflect.Method method = obj.getClass().getDeclaredMethod(methodName, new Class<?>[] { Metadata.class, String.class });
        method.setAccessible(true);
        return method.invoke(obj, args);
    }
}
