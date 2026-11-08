package org.apache.tika.io;

import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

public class EndianUtils_getIntLE_21_0_Test {

    @Test
    public void testGetIntLE() {
        byte[] data = { 0x12, 0x34, 0x56, 0x78 };
        int offset = 0;
        int expected = 0x78563412;
        int result = EndianUtils.getIntLE(data, offset);
        assertEquals(expected, result);
    }
}
