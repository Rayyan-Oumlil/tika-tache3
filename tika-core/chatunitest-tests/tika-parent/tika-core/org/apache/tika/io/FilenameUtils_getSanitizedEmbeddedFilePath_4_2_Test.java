package org.apache.tika.io;

import org.apache.tika.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.utils.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import java.util.Map;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
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

@ExtendWith(MockitoExtension.class)
public class FilenameUtils_getSanitizedEmbeddedFilePath_4_2_Test {

    @Test
    public void testGetSanitizedEmbeddedFilePathWithNullMaxLength() throws Exception {
        // Arrange
        Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "image/png");
        String defaultExtension = ".png";
        Integer maxLength = null;
    }
}
