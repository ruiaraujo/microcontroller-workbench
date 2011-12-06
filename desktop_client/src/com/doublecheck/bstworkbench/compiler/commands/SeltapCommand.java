package com.doublecheck.bstworkbench.compiler.commands;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.CompilerException;
import com.doublecheck.bstworkbench.compiler.Instruction;

public class SeltapCommand extends Command {
        
    private final byte tapNumber;
    public SeltapCommand(final byte state) {
        tapNumber = state; 
    }
    
    public String toString(){
        return "SELTAP " + tapNumber;
    }
    
    /**
     * Parses a line 
     * @param line
     * @return
     * @throws CompilerException 
     */
    public static SeltapCommand parse(Token tok) throws CompilerException{
        if ( tok == null )
            return null;
        final Token whitespace = tok.getNextToken();
        if ( whitespace == null || whitespace.type == Token.NULL )
            throw new CompilerException("Expected numeric argument after SELTAP.");
        final Token argument = whitespace.getNextToken();
        if ( argument == null || argument.type != Token.LITERAL_NUMBER_HEXADECIMAL )
            throw new CompilerException("Expected numeric argument after SELTAP.");    
        SeltapCommand ret = null;
        try{
            ret = new SeltapCommand(Byte.parseByte(argument.getLexeme()));
        }
        catch ( NumberFormatException e ) 
        {
            throw new CompilerException("Error parsing the numeric argument (" + argument.getLexeme() +") after SELTAP.");
        }
        
        String extra = getLineEnd(argument.getNextToken());
        if ( extra.length() > 0 )
            throw new CompilerException("Unexpected tokens after parsing " + ret.tapNumber +
                    " :\n"+extra.toString());
        return ret;
        
    }

    public byte getTapNumber() {
        return tapNumber;
    }

    @Override
    public void checkConsistency() throws CompilerException {
        
    }

    @Override
    public List<Instruction> getInstructions() {
        List<Instruction> ret = new ArrayList<Instruction>(1);
        ret.add(new Instruction(Command.SELTAP, 1, (long)tapNumber));
        return ret;
    }


}
