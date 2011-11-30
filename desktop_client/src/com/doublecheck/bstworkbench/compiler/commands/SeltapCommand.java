package com.doublecheck.bstworkbench.compiler.commands;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;

public class SeltapCommand extends Command {
    
    private final static byte seltapIdentifier = 2;
    
    private final static String CORRECT_REGEX = "seltap \\s*\\d*";
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
    public static SeltapCommand parse(String line) throws ParserException{
        if ( line == null )
            return null;
        final StringTokenizer tok = new StringTokenizer(line.toLowerCase().trim(), " \t");
        String token = tok.nextToken(); // the first token is the command
        SeltapCommand ret = null;
        try{
            token = tok.nextToken();
            ret = new SeltapCommand(Byte.parseByte(token));
        } 
        catch ( NoSuchElementException e ) 
        {
            throw new ParserException("Expected numeric argument after SELTAP.");
        }
        catch ( NumberFormatException e ) 
        {
            throw new ParserException("Error parsing the numeric argument (" + token +") after SELTAP.");
        }
        if ( tok.hasMoreTokens() )
        {
            String extra = "";
            while ( tok.hasMoreTokens() )
                extra += tok.nextToken() + " ";
            throw new ParserException("Unexpected tokens after parsing " + token +
                        " :\n"+extra);
        }
        return ret;       

        
    }

    public byte getTapNumber() {
        return tapNumber;
    }


}
