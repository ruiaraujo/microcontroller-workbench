package com.doublecheck.bstworkbench.compiler;

public class Instruction {
    private final int identifier;
    private final long numberBytes;
    private final Long argument;
    public Instruction(int identifier, long numberBytes, Long argument) {
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
    
}
