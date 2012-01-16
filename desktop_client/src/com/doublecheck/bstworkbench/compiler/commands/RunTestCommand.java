package com.doublecheck.bstworkbench.compiler.commands;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.CompilerException;
import com.doublecheck.bstworkbench.compiler.Instruction;

public class RunTestCommand extends Command {
        
    private final long clockNumber;
    
    public RunTestCommand(final long state) {
    	clockNumber = state; 
    }
    
    public String toString(){
        return "RUNTEST " + clockNumber;
    }
    
    /**
     * Parses a line 
     * @param tok
     * @return
     * @throws CompilerException 
     */
    public static RunTestCommand parse(Token tok) throws CompilerException{
        if ( tok == null )
            return null;
        final Token whitespace = tok.getNextToken();
        if ( whitespace == null || whitespace.type == Token.NULL )
            throw new CompilerException("Expected numeric argument after RUNTEST.");
        final Token argument = whitespace.getNextToken();
        if ( argument == null || argument.type != Token.LITERAL_NUMBER_HEXADECIMAL )
            throw new CompilerException("Expected numeric argument after RUNTEST.");    
        RunTestCommand ret = null;
        try{
            ret = new RunTestCommand(Long.parseLong(argument.getLexeme()));
        }
        catch ( NumberFormatException e ) 
        {
            throw new CompilerException("Error parsing the numeric argument (" + argument.getLexeme() +") after TMS.");
        }
        String extra = getLineEnd(argument.getNextToken());
        if ( extra.length() > 0 )
            throw new CompilerException("Unexpected tokens after parsing " + ret.clockNumber +
                    " :\n"+extra.toString());
        return ret;
        
    }


    @Override
    public void checkConsistency() throws CompilerException {
        if ( clockNumber <= 0 )
            throw new CompilerException("The numeric argument of RUNTEST should be greater than 0.");
    }

    @Override
    public List<Instruction> getInstructions() {
        List<Instruction> ret = new ArrayList<Instruction>();
        TapStateMachine stateMachine = TapStateMachineManager.getInstance().getCurrentTapStateMachine();
        ret.addAll(stateMachine.moveToState("idle"));
        ret.add(new Instruction(Command.RUNTEST, getNumberBytes(clockNumber), clockNumber));
        return ret;
    }


}
