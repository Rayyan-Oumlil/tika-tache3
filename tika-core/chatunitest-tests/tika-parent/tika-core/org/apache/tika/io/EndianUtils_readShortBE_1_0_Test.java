package org.apache.tika.io;

import org.apache.tika.io.EndianUtils;
import org.apache.tika.exception.TikaException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

public class EndianUtils_readShortBE_1_0_Test {

    @Test
    public void testReadShortBE() throws Exception {
        // Create a ByteArrayInputStream with data for testing
        byte[] testData = { 0x12, 0x34 };
        InputStream inputStream = new ByteArrayInputStream(testData);
        // Invoke the private method using reflection
        Method readShortBEMethod = EndianUtils.class.getDeclaredMethod("readShortBE", InputStream.class);
        readShortBEMethod.setAccessible(true);
        short result = (short) readShortBEMethod.invoke(null, inputStream);
        // Verify the result
        assertEquals(0x1234, result);
    }

    @Test
    public void testReadUShortBE() throws Exception {
        // Create a ByteArrayInputStream with data for testing
        byte[] testData = { 0x12, 0x34 };
        InputStream inputStream = new ByteArrayInputStream(testData);
        // Invoke the private method using reflection
        Method readUShortBEMethod = EndianUtils.class.getDeclaredMethod("readUShortBE", InputStream.class);
        readUShortBEMethod.setAccessible(true);
        int result = (int) readUShortBEMethod.invoke(null, inputStream);
        // Verify the result
        assertEquals(0x1234, result);
    }
}
