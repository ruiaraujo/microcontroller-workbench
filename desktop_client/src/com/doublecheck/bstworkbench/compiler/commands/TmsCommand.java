package com.doublecheck.bstworkbench.compiler.commands;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.CompilerException;
import com.doublecheck.bstworkbench.compiler.Instruction;

public class TmsCommand extends Command {
        
    private final byte tmsState;
    
    protected static Instruction TMS_1 = new Instruction(Command.TMS, 1, new BigInteger("1"));
    protected static Instruction TMS_0 = new Instruction(Command.TMS, 1, new BigInteger("0"));
    
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
     * @throws CompilerException 
     */
    public static TmsCommand parse(Token tok) throws CompilerException{
        if ( tok == null )
            return null;
        final Token whitespace = tok.getNextToken();
        if ( whitespace == null || whitespace.type == Token.NULL )
            throw new CompilerException("Expected numeric argument after TMS.");
        final Token argument = whitespace.getNextToken();
        if ( argument == null || argument.type != Token.LITERAL_NUMBER_HEXADECIMAL )
            throw new CompilerException("Expected numeric argument after TMS.");    
        TmsCommand ret = null;
        try{
            ret = new TmsCommand(Byte.parseByte(argument.getLexeme()));
        }
        catch ( NumberFormatException e ) 
        {
            throw new CompilerException("Error parsing the numeric argument (" + argument.getLexeme() +") after TMS.");
        }
        String extra = getLineEnd(argument.getNextToken());
        if ( extra.length() > 0 )
            throw new CompilerException("Unexpected tokens after parsing " + ret.tmsState +
                    " :\n"+extra.toString());
        return ret;
        
    }

    public byte getTmsState() {
        return tmsState;
    }

    @Override
    public void checkConsistency() throws CompilerException {
        if ( tmsState != 0 && tmsState != 1 )
            throw new CompilerException("The numeric argument of TMS can only be 0 or 1");
    }

    @Override
    public List<Instruction> getInstruction() {
        List<Instruction> ret = new ArrayList<Instruction>(1);
        if ( tmsState == 0 )
            ret.add(TMS_0);
        else if ( tmsState == 1  )
            ret.add(TMS_1);
        return ret;
    }


}
