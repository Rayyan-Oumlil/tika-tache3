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

public class EndianUtils_getLongLE_28_1_Test {

    @Test
    public void testGetLongLE() throws IOException {
        byte[] data = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x9A, (byte) 0xBC, (byte) 0xDE, (byte) 0xF0 };
        int offset = 0;
        long expected = 0xFEDCBA9876543212L;
        assertEquals(expected, EndianUtils.getLongLE(data, offset));
    }
}
