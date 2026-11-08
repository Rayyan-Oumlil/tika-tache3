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

public class EndianUtils_getShortLE_12_0_Test {

    @Test
    public void testGetShortLE() throws Exception {
        // Create an instance of the focal class using reflection
        Class<?> clazz = EndianUtils.class;
        Method getShortLEMethod = clazz.getDeclaredMethod("getShortLE", byte[].class, int.class);
        getShortLEMethod.setAccessible(true);
        // Test data
        byte[] testData = new byte[] { 0x12, 0x34 };
        int offset = 0;
        // Invoke the focal method using reflection
        short result = (short) getShortLEMethod.invoke(null, testData, offset);
        // Verify the result
        assertEquals(0x3412, result);
    }
}
