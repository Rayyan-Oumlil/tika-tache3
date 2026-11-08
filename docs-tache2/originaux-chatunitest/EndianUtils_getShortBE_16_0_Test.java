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

public class EndianUtils_getShortBE_16_0_Test {

    @Test
    public void testGetShortBE() throws Exception {
        byte[] data = { 0x12, 0x34 };
        Method method = EndianUtils.class.getDeclaredMethod("getShortBE", byte[].class);
        method.setAccessible(true);
        short result = (short) method.invoke(null, data);
        assertEquals(0x1234, result);
    }
}
