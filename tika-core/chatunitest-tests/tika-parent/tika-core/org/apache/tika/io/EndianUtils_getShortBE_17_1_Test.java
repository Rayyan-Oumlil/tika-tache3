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

public class EndianUtils_getShortBE_17_1_Test {

    @Test
    public void testGetShortBE() throws Exception {
        byte[] data = { 0x12, 0x34 };
        int offset = 0;
        Method method = EndianUtils.class.getDeclaredMethod("getUShortBE", byte[].class, int.class);
        method.setAccessible(true);
        int result = (int) method.invoke(null, data, offset);
        assertEquals(0x1234, result);
    }
}
