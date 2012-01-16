package com.doublecheck.bstworkbench.compiler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Instruction {

    private final byte identifier;
    private final byte numberBytes;
    private final Long argument;
    public Instruction( byte identifier, byte numberBytes, Long argument) {
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
        buf.put((byte) ':');
        buf.put(identifier);
        buf.put(numberBytes);
        buf.put(getBytes(argument, numberBytes));
        return buf.array();
    }
    
    private static byte[] getBytes(Long val, final int numberBytes)
    {
    	/**
    	 * The bytes are placed in little endian order.
    	 */
    	byte [] line = new byte[numberBytes];
    	for ( int i = numberBytes -1 , j = numberBytes -1; i >= 0 ; --j , --i )
    	{
    		line[j] = (byte)(val >>> ( 8 * i));
    	}
    	return line;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argument == null) ? 0 : argument.hashCode());
		result = prime * result + identifier;
		result = prime * result + numberBytes;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instruction other = (Instruction) obj;
		if (argument == null) {
			if (other.argument != null)
				return false;
		} else if (!argument.equals(other.argument))
			return false;
		if (identifier != other.identifier)
			return false;
		if (numberBytes != other.numberBytes)
			return false;
		return true;
	}
    
}
