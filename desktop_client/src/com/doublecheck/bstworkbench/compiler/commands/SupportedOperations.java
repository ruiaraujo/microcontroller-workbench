package com.doublecheck.bstworkbench.compiler.commands;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.CompilerException;

public final class SupportedOperations {
    public final static String SELTAP = "seltap";
    public final static String STATE = "state";
    public final static String SDR = "sdr";
    public final static String SIR = "sir";
    public final static String TMS = "tms";
    public final static String RUNTEST = "runtest";
    
    public static Command parseLine( Token tok ) throws CompilerException{
        if ( tok.getLexeme().toLowerCase().equals(TMS) ) 
            return TmsCommand.parse(tok);
        if ( tok.getLexeme().toLowerCase().equals(SELTAP) )
            return SeltapCommand.parse(tok);
        if ( tok.getLexeme().toLowerCase().equals(STATE)  )
            return StateCommand.parse(tok);
        if ( tok.getLexeme().toLowerCase().equals(SIR) )
            return SirCommand.parse(tok);
        if ( tok.getLexeme().toLowerCase().equals(SDR) )
            return SdrCommand.parse(tok);
        if ( tok.getLexeme().toLowerCase().equals(RUNTEST) )
            return RunTestCommand.parse(tok);
        return null;
        
    }
    
    
}
