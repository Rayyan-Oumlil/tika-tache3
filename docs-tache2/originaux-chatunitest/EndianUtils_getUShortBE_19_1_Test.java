package org.apache.tika.io;

import org.apache.tika.io.EndianUtils;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

public class EndianUtils_getUShortBE_19_1_Test {

    @Test
    public void testGetUShortBE() {
        byte[] data = { 0x12, 0x34 };
        int offset = 0;
        int expected = 0x1234;
        assertEquals(expected, EndianUtils.getUShortBE(data, offset));
    }
}
