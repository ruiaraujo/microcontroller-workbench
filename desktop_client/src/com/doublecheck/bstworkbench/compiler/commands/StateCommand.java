package com.doublecheck.bstworkbench.compiler.commands;

import java.util.List;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.CompilerException;
import com.doublecheck.bstworkbench.compiler.Instruction;

public class StateCommand extends Command {
        
    private final String state;
    public StateCommand(final String state) {
        this.state = state; 
    }
    
    public String toString(){
        return "STATE " + state;
    }
    
    /**
     * Parses a line 
     * @param line
     * @return
     * @throws CompilerException 
     */
    public static StateCommand parse(Token tok) throws CompilerException{
        if ( tok == null )
            return null;
        final Token whitespace = tok.getNextToken();
        if ( whitespace == null || whitespace.type == Token.NULL )
            throw new CompilerException("Expected argument after TMS.");
        final Token argument = whitespace.getNextToken();
        if ( argument == null || argument.type != Token.IDENTIFIER )
            throw new CompilerException("Expected argument after TMS.");    
        StateCommand ret = new StateCommand(argument.getLexeme());
        
        
        String extra = getLineEnd(argument.getNextToken());
        if ( extra.length() > 0 )
            throw new CompilerException("Unexpected tokens after parsing " + ret.state +
                    " :\n"+extra.toString());
        return ret;
        
    }


    public String getState() {
        return state;
    }

    @Override
    public void checkConsistency() throws CompilerException {
        // TODO Do this method after
        
    }

    @Override
    public List<Instruction> getInstruction() {
        // TODO Auto-generated method stub
        return null;
    }


}
