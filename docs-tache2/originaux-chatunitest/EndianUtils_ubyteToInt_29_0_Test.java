package org.apache.tika.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

public class EndianUtils_ubyteToInt_29_0_Test {

    @Test
    public void testUbyteToInt() throws IOException {
        byte b = (byte) 0xFF;
        int result = EndianUtils.ubyteToInt(b);
        assertEquals(255, result);
    }
}
