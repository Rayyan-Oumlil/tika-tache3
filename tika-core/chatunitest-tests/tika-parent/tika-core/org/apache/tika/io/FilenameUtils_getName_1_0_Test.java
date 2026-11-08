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

public class FilenameUtils_getName_1_0_Test {

    @Test
    public void testGetName_withNullPath() {
        String result = FilenameUtils.getName(null);
        assertEquals("", result);
    }

    @Test
    public void testGetName_withEmptyPath() {
        String result = FilenameUtils.getName("");
        assertEquals("", result);
    }

    @Test
    public void testGetName_withUnixPath() {
        String result = FilenameUtils.getName("/home/user/documents/file.txt");
        assertEquals("file.txt", result);
    }

    @Test
    public void testGetName_withWindowsPath() {
        String result = FilenameUtils.getName("C:\\Users\\user\\Documents\\file.txt");
        assertEquals("file.txt", result);
    }

    @Test
    public void testGetName_withMacintoshPath() {
        String result = FilenameUtils.getName("/Volumes/Drive:file.txt");
        assertEquals("file.txt", result);
    }

    @Test
    public void testGetName_withParentDirectory() {
        String result = FilenameUtils.getName("..");
        assertEquals("", result);
    }

    @Test
    public void testGetName_withCurrentDirectory() {
        String result = FilenameUtils.getName(".");
        assertEquals("", result);
    }
}
