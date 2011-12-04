package com.doublecheck.bstworkbench.compiler.commands;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;

public class SirCommand extends SdrCommand {
    protected final static String KEYWORD = "sir";
    
    public SirCommand(final byte numberBytes , final Long tdi, final Long tdo, final Long mask) {
        super(numberBytes, tdi, tdo, mask);
    }

    public String toString(){
        return toString(KEYWORD, numberBytes, tdi, tdo, mask);
    }
    
    /**
     * Parses a line 
     * @param line
     * @return
     * @throws ParserException 
     */
    public static SirCommand parse(Token tok) throws ParserException{
        SdrCommand sdr =  SdrCommand.parseLine(tok, KEYWORD); 
        if ( sdr == null )
            return null;
        return new SirCommand(sdr.numberBytes, sdr.tdi, sdr.tdo, sdr.mask);
    }



}
