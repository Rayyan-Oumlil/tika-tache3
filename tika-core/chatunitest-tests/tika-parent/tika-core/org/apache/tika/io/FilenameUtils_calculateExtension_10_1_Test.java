package org.apache.tika.io;

import org.apache.tika.metadata.Metadata;
import java.util.HashMap;
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
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class FilenameUtils_calculateExtension_10_1_Test {

    private Metadata metadata;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        metadata = mock(Metadata.class);
    }

    @Test
    public void testCalculateExtension_withContentType_png() throws Exception {
        when(metadata.get("Content-Type")).thenReturn("image/png");
        String result = (String) invokePrivateMethod(new FilenameUtils(), "calculateExtension", metadata, ".bin");
        assertEquals(".png", result);
    }

    @Test
    public void testCalculateExtension_withContentType_ocr_png() throws Exception {
        when(metadata.get("Content-Type")).thenReturn("image/ocr-png");
        String result = (String) invokePrivateMethod(new FilenameUtils(), "calculateExtension", metadata, ".bin");
        assertEquals(".png", result);
    }

    @Test
    public void testCalculateExtension_withContentType_null() throws Exception {
        when(metadata.get("Content-Type")).thenReturn(null);
        String result = (String) invokePrivateMethod(new FilenameUtils(), "calculateExtension", metadata, ".bin");
        assertEquals(".bin", result);
    }

    private Object invokePrivateMethod(Object obj, String methodName, Object... args) throws Exception {
        java.lang.reflect.Method method = obj.getClass().getDeclaredMethod(methodName, new Class<?>[] { Metadata.class, String.class });
        method.setAccessible(true);
        return method.invoke(obj, args);
    }
}
