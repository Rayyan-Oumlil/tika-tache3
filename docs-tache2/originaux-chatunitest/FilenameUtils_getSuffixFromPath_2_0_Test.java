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

import org.junit.jupiter.api.*;
import org.mockito.*;

public class FilenameUtils_getSuffixFromPath_2_0_Test {

    @Test
    public void testGetSuffixFromPath() throws Exception {
        // Test case 1: Path with valid suffix
        String path1 = "example.txt";
        assertEquals(".txt", FilenameUtils.getSuffixFromPath(path1));
        // Test case 2: Path with invalid suffix
        String path2 = "example.docx";
        assertEquals("", FilenameUtils.getSuffixFromPath(path2));
        // Test case 3: Path with no extension
        String path3 = "example";
        assertEquals("", FilenameUtils.getSuffixFromPath(path3));
        // Test case 4: Path with multiple dots and valid suffix
        String path4 = "example.file.txt";
        assertEquals(".txt", FilenameUtils.getSuffixFromPath(path4));
        // Test case 5: Path with multiple dots and invalid suffix
        String path5 = "example.file.docx";
        assertEquals("", FilenameUtils.getSuffixFromPath(path5));
        // Test case 6: Path with leading dot and valid suffix
        String path6 = ".example.txt";
        assertEquals(".txt", FilenameUtils.getSuffixFromPath(path6));
        // Test case 7: Path with leading dot and invalid suffix
        String path7 = ".example.docx";
        assertEquals("", FilenameUtils.getSuffixFromPath(path7));
        // Test case 8: Path with trailing dot and valid suffix
        String path8 = "example.txt.";
        assertEquals(".txt", FilenameUtils.getSuffixFromPath(path8));
        // Test case 9: Path with trailing dot and invalid suffix
        String path9 = "example.docx.";
        assertEquals("", FilenameUtils.getSuffixFromPath(path9));
        // Test case 10: Path with empty string
        String path10 = "";
        assertEquals("", FilenameUtils.getSuffixFromPath(path10));
        // Test case 11: Path with null value
        String path11 = null;
        assertEquals("", FilenameUtils.getSuffixFromPath(path11));
    }
}
