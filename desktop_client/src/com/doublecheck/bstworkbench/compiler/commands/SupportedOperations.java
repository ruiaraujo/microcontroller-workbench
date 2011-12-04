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
    private static List<String> supportedOperations = null;
    
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
        return null;
        
    }
    
    public static List<String> getSupportedOperations(){
        if ( supportedOperations == null )
        {
            supportedOperations = new ArrayList<String>();
            //They should be added in alphabetical order!
            supportedOperations.add(SDR);
            supportedOperations.add(SELTAP);
            supportedOperations.add(SIR);
            supportedOperations.add(STATE);
            supportedOperations.add(TMS);
        }
        return supportedOperations;
    }
    
}
