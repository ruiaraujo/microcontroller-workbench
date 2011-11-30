package com.doublecheck.bstworkbench.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.doublecheck.bstworkbench.compiler.commands.Command;
import com.doublecheck.bstworkbench.compiler.commands.SeltapCommand;
import com.doublecheck.bstworkbench.compiler.commands.StateCommand;
import com.doublecheck.bstworkbench.compiler.commands.TmsCommand;

public final class SupportedOperations {
    public final static String SELTAP = "seltap";
    public final static String STATE = "state";
    public final static String SDR = "sdr";
    public final static String SIR = "sir";
    public final static String TMS = "tms";
    private static List<String> supportedOperations = null;
    
    public static Command parseLine( String line ) throws ParserException{
        if ( line.toLowerCase().startsWith(TMS+" ") )
            return TmsCommand.parse(line);
        if ( line.toLowerCase().startsWith(SELTAP+" ") )
            return SeltapCommand.parse(line);
        if ( line.toLowerCase().startsWith(STATE+" ") )
            return StateCommand.parse(line);
        if ( line.toLowerCase().startsWith(SIR+" ") )
            return TmsCommand.parse(line);
        if ( line.toLowerCase().startsWith(SDR+" ") )
            return TmsCommand.parse(line);
        return null;
        
    }
    
    public static List<String> getSupportedOperations(){
        if ( supportedOperations == null )
        {
            supportedOperations = new ArrayList<String>();
            supportedOperations.add(SDR);
            supportedOperations.add(SELTAP);
            supportedOperations.add(SIR);
            supportedOperations.add(STATE);
            supportedOperations.add(TMS);
        }
        return supportedOperations;
    }
    
}
