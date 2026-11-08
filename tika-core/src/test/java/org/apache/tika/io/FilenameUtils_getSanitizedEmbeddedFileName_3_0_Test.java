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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;

public class FilenameUtils_getSanitizedEmbeddedFileName_3_0_Test {

    @Test
    public void testGetSanitizedEmbeddedFileName_withValidPath() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "image/png");
        String path = "/path/to/image.png";
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertEquals("image.png", result);
    }

    @Test
    public void testGetSanitizedEmbeddedFileName_withInvalidPath() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "application/pdf");
        String path = "/path/to/invalid/path";
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertEquals("path.pdf", result);
    }

    @Test
    public void testGetSanitizedEmbeddedFileName_withEmptyPath() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "text/plain");
        String path = "";
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertNull(result);
    }

    @Test
    public void testGetSanitizedEmbeddedFileName_withNullPath() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "application/octet-stream");
        String path = null;
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertNull(result);
    }

    @Test
    public void testGetSanitizedEmbeddedFileName_withLongPath() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "image/jpeg");
        String path = "/path/to/very/long/path/name.jpg";
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertEquals("name.jpg", result);
    }

    @Test
    public void testGetSanitizedEmbeddedFileName_withReservedCharacters() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "application/msword");
        String path = "/path/to/reserved/characters?*<>|\"'\\";
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertNull(result);
    }

    @Test
    public void testGetSanitizedEmbeddedFileName_withProtocolPrefix() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "application/vnd.ms-excel");
        String path = "http://example.com/path/to/file.xlsx";
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertEquals("file.xlsx", result);
    }

    @Test
    public void testGetSanitizedEmbeddedFileName_withPrefix() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "application/pdf");
        String path = "/path/to/file.pdf";
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertEquals("file.pdf", result);
    }

    @Test
    public void testGetSanitizedEmbeddedFileName_withEmptyExtension() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "application/pdf");
        String path = "/path/to/file";
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertEquals("file.pdf", result);
    }

    private static String invokePrivateMethod(Class<?> clazz, String methodName, Object... args) throws Exception {
        Metadata metadata = (Metadata) args[0];
        if (args[1] != null) {
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, (String) args[1]);
        }
        return FilenameUtils.getSanitizedEmbeddedFileName(metadata, ".bin", (Integer) args[2]);
    }
}
