/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import org.apache.tika.io.EndianUtils.BufferUnderrunException;

/**
 * IFT3913 tâche 2 : tests écrits à la main pour les mutants laissés vivants
 * par les tests originaux et par les tests générés par ChatUniTest.
 */
public class EndianUtilsTache2Test {

    private static InputStream stream(int... bytes) {
        byte[] data = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            data[i] = (byte) bytes[i];
        }
        return new ByteArrayInputStream(data);
    }

    // Octets tous distincts : chaque octet atterrit à une position différente selon l'ordre de lecture.
    private static final int[] EIGHT_BYTES = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x88};

    @Test
    public void readLongLittleAndBigEndianPlaceEachByteAtItsOwnPosition() throws Exception {
        assertEquals(0x8807060504030201L, EndianUtils.readLongLE(stream(EIGHT_BYTES)));
        assertEquals(0x0102030405060788L, EndianUtils.readLongBE(stream(EIGHT_BYTES)));
    }

    @Test
    public void readIntFamilyUsesTheRightByteOrder() throws Exception {
        int[] bytes = {0x01, 0x02, 0x03, 0x84};
        assertEquals(0x84030201, EndianUtils.readIntLE(stream(bytes)));
        assertEquals(0x01020384, EndianUtils.readIntBE(stream(bytes)));
        assertEquals(0x02018403, EndianUtils.readIntME(stream(bytes)));
        assertEquals(0x84030201L, EndianUtils.readUIntLE(stream(bytes)));
        assertEquals(0x01020384L, EndianUtils.readUIntBE(stream(bytes)));
    }

    @Test
    public void readShortFamilyDistinguishesSignedFromUnsigned() throws Exception {
        assertEquals(0x8001, EndianUtils.readUShortLE(stream(0x01, 0x80)));
        assertEquals((short) 0x8001, EndianUtils.readShortLE(stream(0x01, 0x80)));
        assertEquals(0x0180, EndianUtils.readUShortBE(stream(0x01, 0x80)));
    }

    @Test
    public void readsOfOnlyZeroBytesAreValidAndDoNotThrow() throws Exception {
        assertEquals(0, EndianUtils.readUShortLE(stream(0, 0)));
        assertEquals(0, EndianUtils.readUShortBE(stream(0, 0)));
        assertEquals(0L, EndianUtils.readUIntLE(stream(0, 0, 0, 0)));
        assertEquals(0L, EndianUtils.readUIntBE(stream(0, 0, 0, 0)));
        assertEquals(0, EndianUtils.readIntLE(stream(0, 0, 0, 0)));
        assertEquals(0, EndianUtils.readIntBE(stream(0, 0, 0, 0)));
        assertEquals(0, EndianUtils.readIntME(stream(0, 0, 0, 0)));
        assertEquals(0L, EndianUtils.readLongLE(stream(0, 0, 0, 0, 0, 0, 0, 0)));
        assertEquals(0L, EndianUtils.readLongBE(stream(0, 0, 0, 0, 0, 0, 0, 0)));
    }

    @Test
    public void streamThatEndsOneByteTooEarlyIsAnUnderrun() {
        // Seul le dernier read() renvoie -1 : le test (a | b | ...) < 0 doit le voir.
        assertThrows(BufferUnderrunException.class, () -> EndianUtils.readUShortLE(stream(0x41)));
        assertThrows(BufferUnderrunException.class, () -> EndianUtils.readUShortBE(stream(0x41)));
        assertThrows(BufferUnderrunException.class, () -> EndianUtils.readUIntLE(stream(0x41, 0x41, 0x41)));
        assertThrows(BufferUnderrunException.class, () -> EndianUtils.readUIntBE(stream(0x41, 0x41, 0x41)));
        assertThrows(BufferUnderrunException.class, () -> EndianUtils.readIntLE(stream(0x41, 0x41, 0x41)));
        assertThrows(BufferUnderrunException.class, () -> EndianUtils.readIntBE(stream(0x41, 0x41, 0x41)));
        assertThrows(BufferUnderrunException.class, () -> EndianUtils.readIntME(stream(0x41, 0x41, 0x41)));
        assertThrows(BufferUnderrunException.class,
                () -> EndianUtils.readLongLE(stream(0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41)));
        assertThrows(BufferUnderrunException.class,
                () -> EndianUtils.readLongBE(stream(0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41)));
    }

    @Test
    public void readUE7AccumulatesSevenBitGroupsAndAcceptsAZeroTerminator() throws Exception {
        // 0x81 = « continue, valeur 1 », 0x00 = « dernier groupe, valeur 0 » -> (1 << 7) + 0
        assertEquals(128L, EndianUtils.readUE7(stream(0x81, 0x00)));
    }

    @Test
    public void readUE7StopsAfterSixContinuationBytes() throws Exception {
        // Au-delà de 6 octets, le 7e octet lu est ignoré : 6 groupes de valeur 1.
        long sixGroupsOfOne = 0b000001_0000001_0000001_0000001_0000001_0000001L;
        assertEquals(sixGroupsOfOne, EndianUtils.readUE7(stream(0x81, 0x81, 0x81, 0x81, 0x81, 0x81, 0x81)));
    }

    @Test
    public void readUE7ThrowsWhenTheStreamEndsBeforeTheLastGroup() {
        assertThrows(IOException.class, () -> EndianUtils.readUE7(stream(0x81)));
    }

    @Test
    public void overloadsWithoutOffsetReadFromTheStartOfTheArray() {
        byte[] data = {(byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFC};
        assertEquals((short) 0xFEFF, EndianUtils.getShortLE(data));
        assertEquals(0xFCFDFEFFL, EndianUtils.getUIntLE(data));
        assertEquals(0xFFFEFDFCL, EndianUtils.getUIntBE(data));
        assertEquals(0xFFFEFDFCL, EndianUtils.getUIntBE(data, 0));
    }
}
