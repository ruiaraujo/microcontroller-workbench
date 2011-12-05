package com.doublecheck.bstworkbench.compiler.commands;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.CompilerException;
import com.doublecheck.bstworkbench.compiler.Instruction;

public class SirCommand extends SdrCommand {
    protected final static String KEYWORD = "SIR";
    
    public SirCommand(final byte numberBytes , final Long tdi, final Long tdo, final Long mask) {
        super(numberBytes, tdi, tdo, mask);
    }

    public String toString(){
        return toString(KEYWORD, numberBits, tdi, tdo, mask);
    }
    
    /**
     * Parses a line 
     * @param line
     * @return
     * @throws CompilerException 
     */
    public static SirCommand parse(Token tok) throws CompilerException{
        SdrCommand sdr =  SdrCommand.parseLine(tok, KEYWORD); 
        if ( sdr == null )
            return null;
        return new SirCommand(sdr.numberBits, sdr.tdi, sdr.tdo, sdr.mask);
    }

    @Override
    public List<Instruction> getInstructions() {
        TapStateMachine stateMachine = TapStateMachine.getInstance();
        List<Instruction> ret = new ArrayList<Instruction>();
        ret.addAll(stateMachine.moveToState("shift-ir"));
        int numberBytes = numberBits/8;
        if ( numberBytes*8 < numberBits )
            numberBytes++;
        ret.add(new Instruction(Command.TDI, numberBytes, tdi));
        if ( tdo != null )
        {
            ret.add(new Instruction(Command.TDO, numberBytes, tdo));
            ret.add(new Instruction(Command.MASK, numberBytes, mask));

        }
        return ret;
    }

}
