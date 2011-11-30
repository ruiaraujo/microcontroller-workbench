package com.doublecheck.bstworkbench.compiler.commands;


/**
 * Abastract class which holds some common methods from
 * the commands
 * @author ruka
 *
 */
public abstract class Command {
    
    private final byte identifier;
    public Command( final byte identifier){
        this.identifier = identifier;
    }
    public byte getIdentifier() {
        return identifier;
    }

}
