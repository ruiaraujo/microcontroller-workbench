package com.doublecheck.bstworkbench.compiler;

import java.math.BigInteger;

public class Instruction {
    private final int identifier;
    private final long numberBytes;
    private final BigInteger argument;
    public Instruction(int identifier, long numberBytes, BigInteger argument) {
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
    public BigInteger getArgument() {
        return argument;
    }
    
}
