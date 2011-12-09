package com.doublecheck.bstworkbench.compiler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Instruction {
    private final byte identifier;
    private final byte numberBytes;
    private final Long argument;
    public Instruction(byte identifier, byte numberBytes, Long argument) {
        super();
        this.identifier = identifier;
        this.numberBytes = numberBytes;
        this.argument = argument;
    }
    public int getIdentifier() {
        return identifier;
    }
    public long getNumberBytes() {
        return numberBytes;
    }
    public Long getArgument() {
        return argument;
    }
    @Override
    public String toString() {
        return "Instruction [argument=" + Long.toHexString(argument) + ", identifier="
                + identifier + ", numberBytes=" + numberBytes + "]";
    }
    
    public byte[] toFile() {
        ByteBuffer buf = ByteBuffer.allocate(3+numberBytes);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put((byte)58);
        buf.put(identifier);
        buf.put(numberBytes);
        buf.put(getBytes(argument, numberBytes));
        return buf.array();
    }
    
    private static byte[] getBytes(Long val, final int numberBytes)
    {
    	byte [] line = new byte[numberBytes];
    	for ( int i = numberBytes -1 , j = 0; i >= 0 ; ++j , --i )
    	{
    		line[j] = (byte)(val >>> ( 8 * i));
    	}
    	return line;
    }
    
}
