package com.doublecheck.bstworkbench.compiler.commands;


import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;

public class SeltapCommand extends Command {
    
    private final static byte seltapIdentifier = 2;
    
    private final byte tapNumber;
    public SeltapCommand(final byte state) {
        super(seltapIdentifier );
        tapNumber = state; 
    }
    
    public String toString(){
        return "SELTAP " + tapNumber;
    }
    
    /**
     * Parses a line 
     * @param line
     * @return
     * @throws ParserException 
     */
    public static SeltapCommand parse(Token tok) throws ParserException{
        if ( tok == null )
            return null;
        final Token whitespace = tok.getNextToken();
        if ( whitespace == null || whitespace.type == Token.NULL )
            throw new ParserException("Expected numeric argument after SELTAP.");
        final Token argument = whitespace.getNextToken();
        if ( argument == null || argument.type != Token.LITERAL_NUMBER_HEXADECIMAL )
            throw new ParserException("Expected numeric argument after SELTAP.");    
        SeltapCommand ret = null;
        try{
            ret = new SeltapCommand(Byte.parseByte(argument.getLexeme()));
        }
        catch ( NumberFormatException e ) 
        {
            throw new ParserException("Error parsing the numeric argument (" + argument.getLexeme() +") after SELTAP.");
        }
        
        String extra = getLineEnd(argument.getNextToken());
        if ( extra.length() > 0 )
            throw new ParserException("Unexpected tokens after parsing " + ret.tapNumber +
                    " :\n"+extra.toString());
        return ret;
        
    }

    public byte getTapNumber() {
        return tapNumber;
    }


}
