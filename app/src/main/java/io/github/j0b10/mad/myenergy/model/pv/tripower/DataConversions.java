package io.github.j0b10.mad.myenergy.model.pv.tripower;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public interface DataConversions {

    static BigInteger uint64(byte[] registers) throws IllegalArgumentException {
        if (registers.length != 8)
            throw new IllegalArgumentException("uint64 must be 4 registers (8 byte array)");
        return new BigInteger(1, registers);
    }

    static long uint32(byte[] registers) throws IllegalArgumentException {
        if (registers.length != 4)
            throw new IllegalArgumentException("uint32 must be 2 registers (4 byte array)");
        return Integer.toUnsignedLong(ByteBuffer.wrap(registers).getInt());
    }

    static int uint16(byte[] registers) throws IllegalArgumentException {
        if (registers.length != 2)
            throw new IllegalArgumentException("uint16 must be 1 register (2 byte array)");
        return Short.toUnsignedInt(ByteBuffer.wrap(registers).getShort());
    }

    static short sint16(byte[] registers) throws IllegalArgumentException {
        if (registers.length != 2)
            throw new IllegalArgumentException("uint16 must be 1 register (2 byte array)");
        return ByteBuffer.wrap(registers).getShort();
    }

    static int sint32(byte[] registers) throws IllegalArgumentException {
        if (registers.length != 4)
            throw new IllegalArgumentException("uint16 must be 2 registers (4 byte array)");
        return ByteBuffer.wrap(registers).getInt();
    }
}
