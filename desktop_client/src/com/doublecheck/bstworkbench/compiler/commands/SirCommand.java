package com.doublecheck.bstworkbench.compiler.commands;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.CompilerException;

public class SirCommand extends SdrCommand {
    protected final static String KEYWORD = "SIR";
    
    public SirCommand(final byte numberBytes , final Long tdi, final Long tdo, final Long mask) {
        super(numberBytes, tdi, tdo, mask);
    }

    public String toString(){
        return toString(KEYWORD, numberBits, tdi, tdo, mask);
    }
    
    /**
     * Parses a line 
     * @param line
     * @return
     * @throws CompilerException 
     */
    public static SirCommand parse(Token tok) throws CompilerException{
        SdrCommand sdr =  SdrCommand.parseLine(tok, KEYWORD); 
        if ( sdr == null )
            return null;
        return new SirCommand(sdr.numberBits, sdr.tdi, sdr.tdo, sdr.mask);
    }



}
