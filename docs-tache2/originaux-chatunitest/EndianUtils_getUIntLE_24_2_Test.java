package org.apache.tika.io;

import org.apache.tika.io.EndianUtils;
import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class EndianUtils_getUIntLE_24_2_Test {

    @Test
    public void testGetUIntLE() throws Exception {
        // Create an instance of the focal class
        EndianUtils endianUtils = new EndianUtils();
        // Get the private method using reflection
        Method getUIntLEMethod = EndianUtils.class.getDeclaredMethod("getUIntLE", byte[].class, int.class);
        getUIntLEMethod.setAccessible(true);
        // Test cases
        byte[] data1 = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };
        long result1 = (long) getUIntLEMethod.invoke(endianUtils, data1, 0);
        assertEquals(1, result1, "Test case 1 failed");
        byte[] data2 = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF };
        long result2 = (long) getUIntLEMethod.invoke(endianUtils, data2, 6);
        assertEquals(4294967295L, result2, "Test case 2 failed");
        byte[] data3 = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
        long result3 = (long) getUIntLEMethod.invoke(endianUtils, data3, 0);
        assertEquals(1296893664L, result3, "Test case 3 failed");
    }
}
