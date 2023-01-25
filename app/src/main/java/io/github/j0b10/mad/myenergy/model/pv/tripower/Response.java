package io.github.j0b10.mad.myenergy.model.pv.tripower;

import java.math.BigInteger;
import java.util.Arrays;

public class Response {

    private final int start;
    private final byte[] data;

    public Response(int start, byte[] data) {
        this.start = start;
        this.data = data;
    }

    public BigInteger getUint64(int register) {
        return DataConversions.uint64(getRegisters(register, 4));
    }

    public long getUint32(int register) {
        return DataConversions.uint32(getRegisters(register, 2));
    }

    public int getUint16(int register) {
        return DataConversions.uint16(getRegisters(register, 1));
    }

    public short getSint16(int register) {
        return DataConversions.sint16(getRegisters(register, 1));
    }

    public int getSint32(int register) {
        return DataConversions.sint32(getRegisters(register, 2));
    }

    public byte[] getRegisters(int register, int amount) {
        amount *= 2; //each register is 2 bytes
        if (register < start) throw new ArrayIndexOutOfBoundsException(register);
        else if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        else if (register + amount > start + data.length)
            throw new ArrayIndexOutOfBoundsException(register + amount - 1);
        int i = (register - start) * 2, j = i + amount;
        return Arrays.copyOfRange(data, i, j);
    }

    public int getRegister() {
        return start;
    }

    public int length() {
        return data.length / 2;
    }
}
