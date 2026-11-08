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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;

/**
 * IFT3913 tâche 2 : tests écrits à la main pour les mutants laissés vivants
 * par les tests originaux et par les tests générés par ChatUniTest.
 */
public class FilenameUtilsTache2Test {

    private static final int NO_LIMIT = 100;

    private static Metadata with(Property key, String value) {
        Metadata metadata = new Metadata();
        metadata.set(key, value);
        return metadata;
    }

    private static String fileName(Metadata metadata, int maxLength) {
        return FilenameUtils.getSanitizedEmbeddedFileName(metadata, ".bin", maxLength);
    }

    private static String filePath(Metadata metadata, int maxLength) {
        return FilenameUtils.getSanitizedEmbeddedFilePath(metadata, ".bin", maxLength);
    }

    @Test
    public void fileNameFallsBackOnEachMetadataKeyWhenItIsTheOnlyOneSet() {
        assertEquals("internal.txt", fileName(with(TikaCoreProperties.INTERNAL_PATH, "zip/internal.txt"), NO_LIMIT));
        assertEquals("rId7.bin", fileName(with(TikaCoreProperties.EMBEDDED_RELATIONSHIP_ID, "rId7"), NO_LIMIT));
        assertEquals("embedded.pdf", fileName(with(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, "/doc/embedded.pdf"), NO_LIMIT));
        assertEquals("orig.xlsx", fileName(with(TikaCoreProperties.ORIGINAL_RESOURCE_NAME, "orig.xlsx"), NO_LIMIT));
    }

    @Test
    public void fileNamePrefersTheResourceNameOverTheOtherKeys() {
        Metadata metadata = with(TikaCoreProperties.RESOURCE_NAME_KEY, "first.txt");
        metadata.set(TikaCoreProperties.INTERNAL_PATH, "second.txt");
        metadata.set(TikaCoreProperties.EMBEDDED_RELATIONSHIP_ID, "third.txt");
        assertEquals("first.txt", fileName(metadata, NO_LIMIT));
    }

    @Test
    public void filePathFallsBackOnEachMetadataKeyWhenItIsTheOnlyOneSet() {
        assertEquals("a/b.txt", filePath(with(TikaCoreProperties.INTERNAL_PATH, "a/b.txt"), NO_LIMIT));
        assertEquals("c.txt", filePath(with(TikaCoreProperties.RESOURCE_NAME_KEY, "c.txt"), NO_LIMIT));
        assertEquals("rId9.bin", filePath(with(TikaCoreProperties.EMBEDDED_RELATIONSHIP_ID, "rId9"), NO_LIMIT));
        assertEquals("o.docx", filePath(with(TikaCoreProperties.ORIGINAL_RESOURCE_NAME, "o.docx"), NO_LIMIT));
    }

    @Test
    public void filePathPrefersTheEmbeddedResourcePathOverTheOtherKeys() {
        Metadata metadata = with(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, "/dir/sub/file.pdf");
        metadata.set(TikaCoreProperties.INTERNAL_PATH, "other/internal.pdf");
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, "name.pdf");
        assertEquals("dir/sub/file.pdf", filePath(metadata, NO_LIMIT));
    }

    @Test
    public void fileNameIsTruncatedOnlyWhenTheNamePartIsStrictlyLongerThanMaxLength() {
        assertEquals("abcdefghij.txt", fileName(with(TikaCoreProperties.RESOURCE_NAME_KEY, "abcdefghij.txt"), 10));
        assertEquals("abc....txt", fileName(with(TikaCoreProperties.RESOURCE_NAME_KEY, "abcdefghijk.txt"), 10));
    }

    @Test
    public void filePathKeepsTheDirectoryOnlyWhileTheWholePathFitsInMaxLength() {
        Metadata metadata = with(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, "dir/name.txt");
        assertEquals("dir/name.txt", filePath(metadata, 12));
        assertEquals("name.txt", filePath(metadata, 11));
    }

    @Test
    public void filePathTruncatesTheNameOnlyWhenItIsStrictlyLongerThanMaxLength() {
        assertEquals("abcdefgh.txt", filePath(with(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, "dir/abcdefgh.txt"), 8));
        assertEquals("a....txt", filePath(with(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, "dir/abcdefghi.txt"), 8));
    }

    @Test
    public void blankNamesGiveNullRatherThanAnEmptyName() {
        assertNull(fileName(with(TikaCoreProperties.RESOURCE_NAME_KEY, "   .txt"), NO_LIMIT));
        assertNull(filePath(with(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, "dir/.."), NO_LIMIT));
        // ".txt" n'a plus de nom une fois l'extension retirée
        assertNull(filePath(with(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, "dir/.txt"), NO_LIMIT));
    }

    @Test
    public void driveLettersAtBothEndsOfTheAlphabetAreStrippedOnEverySystem() {
        // sous Linux, commons-io renvoie 0 pour "A:" : c'est le repli de Tika qui retire le préfixe.
        // Sans ce repli, le chemin deviendrait "A" puis "A.bin".
        assertNull(filePath(with(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, "A:"), NO_LIMIT));
        assertNull(filePath(with(TikaCoreProperties.EMBEDDED_RESOURCE_PATH, "Z:"), NO_LIMIT));
    }

    @Test
    public void suffixOfSixCharactersIncludingTheDotIsRejected() {
        assertEquals(".abcd", FilenameUtils.getSuffixFromPath("file.abcd"));
        assertEquals("", FilenameUtils.getSuffixFromPath("file.abcde"));
    }

    @Test
    public void unknownContentTypeFallsBackToBin() {
        Metadata metadata = with(Metadata.CONTENT_TYPE, "application/x-ift3913-inconnu");
        assertEquals(".bin", FilenameUtils.calculateExtension(metadata, ".default"));
    }

    @Test
    public void resolveWithinAcceptsAFileThatExistsInsideTheDirectory(@TempDir Path dir) throws Exception {
        Path file = Files.createFile(dir.resolve("a.txt"));
        assertEquals(file.normalize(), FilenameUtils.resolveWithin(dir, "a.txt"));
    }

    @Test
    public void resolveWithinAcceptsAFileThatDoesNotExistYet(@TempDir Path dir) throws Exception {
        assertEquals(dir.resolve("future.txt").normalize(), FilenameUtils.resolveWithin(dir, "future.txt"));
    }

    @Test
    public void resolveWithinRejectsASymlinkThatEscapesTheDirectory(@TempDir Path root) throws Exception {
        Path dir = Files.createDirectory(root.resolve("dir"));
        Path outside = Files.createFile(root.resolve("secret.txt"));
        Path link = dir.resolve("link.txt");
        try {
            Files.createSymbolicLink(link, outside);
        } catch (UnsupportedOperationException | IOException | SecurityException e) {
            // Windows sans mode développeur : impossible de créer un lien symbolique
            assumeTrue(false, "symbolic links not supported here: " + e);
        }
        assertThrows(IOException.class, () -> FilenameUtils.resolveWithin(dir, "link.txt"));
    }
}
