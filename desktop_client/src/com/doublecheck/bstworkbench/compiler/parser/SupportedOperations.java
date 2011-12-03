package com.doublecheck.bstworkbench.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.doublecheck.bstworkbench.compiler.commands.Command;
import com.doublecheck.bstworkbench.compiler.commands.SdrCommand;
import com.doublecheck.bstworkbench.compiler.commands.SeltapCommand;
import com.doublecheck.bstworkbench.compiler.commands.SirCommand;
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
        if ( line.toLowerCase().equals(TMS) ||
            line.toLowerCase().startsWith(TMS+" ") ) 
            return TmsCommand.parse(line);
        if ( line.toLowerCase().equals(SELTAP) ||
                line.toLowerCase().startsWith(SELTAP+" ") )
            return SeltapCommand.parse(line);
        if ( line.toLowerCase().equals(STATE) ||
                line.toLowerCase().startsWith(STATE+" ") )
            return StateCommand.parse(line);
        if ( line.toLowerCase().equals(SIR) ||
                line.toLowerCase().startsWith(SIR+" ") )
            return SirCommand.parse(line);
        if ( line.toLowerCase().equals(SDR) ||
                line.toLowerCase().startsWith(SDR+" ") )
            return SdrCommand.parse(line);
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
