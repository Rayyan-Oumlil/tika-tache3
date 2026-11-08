package org.apache.tika.io;

import org.apache.tika.io.EndianUtils;
import java.lang.reflect.Method;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;

public class EndianUtils_getIntBE_22_0_Test {

    @Test
    public void testGetIntBE() throws Exception {
        byte[] data = { 0x12, 0x34, 0x56, 0x78 };
        int expected = 0x12345678;
        Method method = EndianUtils.class.getDeclaredMethod("getIntBE", byte[].class);
        method.setAccessible(true);
        int result = (int) method.invoke(null, data);
        assertEquals(expected, result);
    }
}
