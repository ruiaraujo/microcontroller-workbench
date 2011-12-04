package com.doublecheck.bstworkbench.compiler.commands;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;

public class TmsCommand extends Command {
        
    private final byte tmsState;
    public TmsCommand(final byte state) {
        tmsState = state; 
    }
    
    public String toString(){
        return "TMS " + tmsState;
    }
    
    /**
     * Parses a line 
     * @param tok2
     * @return
     * @throws ParserException 
     */
    public static TmsCommand parse(Token tok) throws ParserException{
        if ( tok == null )
            return null;
        final Token whitespace = tok.getNextToken();
        if ( whitespace == null || whitespace.type == Token.NULL )
            throw new ParserException("Expected numeric argument after TMS.");
        final Token argument = whitespace.getNextToken();
        if ( argument == null || argument.type != Token.LITERAL_NUMBER_HEXADECIMAL )
            throw new ParserException("Expected numeric argument after TMS.");    
        TmsCommand ret = null;
        try{
            ret = new TmsCommand(Byte.parseByte(argument.getLexeme()));
        }
        catch ( NumberFormatException e ) 
        {
            throw new ParserException("Error parsing the numeric argument (" + argument.getLexeme() +") after TMS.");
        }
        String extra = getLineEnd(argument.getNextToken());
        if ( extra.length() > 0 )
            throw new ParserException("Unexpected tokens after parsing " + ret.tmsState +
                    " :\n"+extra.toString());
        return ret;
        
    }

    public byte getTmsState() {
        return tmsState;
    }


}
