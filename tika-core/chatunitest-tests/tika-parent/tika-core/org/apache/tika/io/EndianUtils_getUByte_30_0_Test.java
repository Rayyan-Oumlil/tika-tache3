package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

public class EndianUtils_getUByte_30_0_Test {

    @Test
    public void testGetUByte() {
        byte[] data = { 0x12, 0x34, 0x56, 0x78 };
        int offset = 0;
        short expected = 0x12;
        short result = EndianUtils.getUByte(data, offset);
        assertEquals(expected, result);
    }
}
