package com.doublecheck.bstworkbench.compiler.commands;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;

public class TmsCommand extends Command {
    
    private final static byte tmsIdentifier = 1;
    
    private final static String CORRECT_REGEX = "tms \\s*\\d*";
    private final byte tmsState;
    public TmsCommand(final byte state) {
        super(tmsIdentifier );
        tmsState = state; 
    }
    
    public String toString(){
        return "TMS " + tmsState;
    }
    
    /**
     * Parses a line 
     * @param line
     * @return
     * @throws ParserException 
     */
    public static TmsCommand parse(String line) throws ParserException{
        if ( line == null )
            return null;
        final StringTokenizer tok = new StringTokenizer(line.toLowerCase().trim(), " \t");
        String token = tok.nextToken(); // the first token is the command
        TmsCommand ret = null;
        try{
            token = tok.nextToken();
            ret = new TmsCommand(Byte.parseByte(token));
        } 
        catch ( NoSuchElementException e ) 
        {
            throw new ParserException("Expected numeric argument after TMS.");
        }
        catch ( NumberFormatException e ) 
        {
            throw new ParserException("Error parsing the numeric argument (" + token +") after TMS.");
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

    public byte getTmsState() {
        return tmsState;
    }


}
