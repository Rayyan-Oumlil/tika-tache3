package org.apache.tika.io;

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
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.utils.StringUtils;

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
