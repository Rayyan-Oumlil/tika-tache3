package org.apache.tika.io;

import org.apache.tika.io.FilenameUtils;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.extractor.EmbeddedDocumentUtil;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.utils.StringUtils;

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
