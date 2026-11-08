package org.apache.tika.io;

import org.apache.tika.metadata.Metadata;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
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
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.utils.StringUtils;

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
        assertNull(result);
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
        assertEquals("name...jpg", result);
    }

    @Test
    public void testGetSanitizedEmbeddedFileName_withReservedCharacters() throws Exception {
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "application/msword");
        String path = "/path/to/reserved/characters?*<>|\"'\\";
        int maxLength = 10;
        String result = invokePrivateMethod(FilenameUtils.class, "getSanitizedEmbeddedFileName", metadata, path, maxLength);
        assertEquals("reserved%25characters_._jpg", result);
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
        assertNull(result);
    }

    private static String invokePrivateMethod(Class<?> clazz, String methodName, Object... args) throws Exception {
        Method method = clazz.getDeclaredMethod(methodName, new Class<?>[] { Metadata.class, String.class, int.class });
        method.setAccessible(true);
        return (String) method.invoke(null, args);
    }
}
