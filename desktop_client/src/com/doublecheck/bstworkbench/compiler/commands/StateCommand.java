package com.doublecheck.bstworkbench.compiler.commands;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;

public class StateCommand extends Command {
    
    private final static byte stateIdentifier = 3;
    
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
    public static StateCommand parse(Token tok) throws ParserException{
        if ( tok == null )
            return null;
        final Token whitespace = tok.getNextToken();
        if ( whitespace == null || whitespace.type == Token.NULL )
            throw new ParserException("Expected argument after TMS.");
        final Token argument = whitespace.getNextToken();
        if ( argument == null || argument.type != Token.IDENTIFIER )
            throw new ParserException("Expected argument after TMS.");    
        StateCommand ret = new StateCommand(argument.getLexeme());
        
        
        String extra = getLineEnd(argument.getNextToken());
        if ( extra.length() > 0 )
            throw new ParserException("Unexpected tokens after parsing " + ret.state +
                    " :\n"+extra.toString());
        return ret;
        
    }


    public String getState() {
        return state;
    }


}
