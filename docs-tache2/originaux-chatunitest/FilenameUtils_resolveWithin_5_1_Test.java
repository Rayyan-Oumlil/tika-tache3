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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.*;
import org.mockito.*;

public class FilenameUtils_resolveWithin_5_1_Test {

    private FilenameUtils filenameUtils;

    @BeforeEach
    public void setUp() {
        filenameUtils = new FilenameUtils();
    }

    @Test
    public void testResolveWithin_NormalPath() throws IOException {
        Path dir = Paths.get("/home/user/documents");
        String name = "report.pdf";
        Path expected = Paths.get("/home/user/documents/report.pdf");
        Path result = filenameUtils.resolveWithin(dir, name);
        assertEquals(expected, result);
    }

    @Test
    public void testResolveWithin_SymlinkTraversal_Invalid() throws IOException {
        Path dir = Paths.get("/home/user/documents");
        String name = "../symlink/../report.pdf";
        assertThrows(IOException.class, () -> filenameUtils.resolveWithin(dir, name));
    }
}
