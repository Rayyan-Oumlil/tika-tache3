package org.apache.tika.io;

import java.io.IOException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

public class EndianUtils_getShortLE_13_0_Test {

    @Test
    public void testGetShortLE() throws IOException {
        byte[] data = new byte[] { 0x12, 0x34 };
        int offset = 0;
        short expected = (short) ((data[offset + 1] & 0xFF) << 8 | (data[offset] & 0xFF));
        assertEquals(expected, EndianUtils.getShortLE(data, offset));
    }
}
