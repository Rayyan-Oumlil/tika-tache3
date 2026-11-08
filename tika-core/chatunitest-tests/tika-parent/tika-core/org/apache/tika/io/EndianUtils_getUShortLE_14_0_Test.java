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

public class EndianUtils_getUShortLE_14_0_Test {

    @Test
    public void testGetUShortLE() {
        byte[] data = { 0x12, 0x34 };
        int result = EndianUtils.getUShortLE(data);
        assertEquals(0x3412, result);
    }

    @Test
    public void testGetUShortLEWithOffset() {
        byte[] data = { 0x00, 0x12, 0x34, 0x56 };
        int result = EndianUtils.getUShortLE(data, 1);
        assertEquals(0x3412, result);
    }
}
