package org.apache.tika.io;

import org.apache.tika.io.FilenameUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
