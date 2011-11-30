package com.doublecheck.bstworkbench.compiler.commands;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;

public class StateCommand extends Command {
    
    private final static byte stateIdentifier = 3;
    
    private final static String CORRECT_REGEX = "state \\s*\\w*";
    private final String state;
    public StateCommand(final String state) {
        super(stateIdentifier );
        this.state = state; 
    }
    
    public String toString(){
        return "STATE " + state;
    }
    
    /**
     * Parses a line 
     * @param line
     * @return
     * @throws ParserException 
     */
    public static StateCommand parse(String line) throws ParserException{
        if ( line == null )
            return null;
        final StringTokenizer tok = new StringTokenizer(line.toLowerCase().trim(), " \t");
        String token = tok.nextToken(); // the first token is the command
        try{
            token = tok.nextToken();
        } 
        catch ( NoSuchElementException e ) 
        {
            throw new ParserException("Expected argument after STATE.");
        }
        
        if ( tok.hasMoreTokens() )
        {
            String extra = "";
            while ( tok.hasMoreTokens() )
                extra += tok.nextToken() + " ";
            throw new ParserException("Unexpected tokens after parsing " + token +
                        " :\n"+extra);
        }
        return new StateCommand(token);        
    }

    public String getState() {
        return state;
    }


}
