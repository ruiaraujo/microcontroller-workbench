package com.doublecheck.bstworkbench.compiler.commands;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;

public class SdrCommand extends Command {
    
    private final static byte sdrIdentifier = 4;
    
    protected final static String TDI = "tdi";
    protected final static String TDO = "tdo";
    protected final static String MASK = "mask";
    
    protected final Long tdi;
    protected final Long tdo;
    protected final Long mask;
    
    
    public SdrCommand(final Long tdi, final Long tdo, final Long mask) {
        super(sdrIdentifier);
        this.tdi = tdi;
        this.tdo = tdo;
        this.mask = mask;
    }

    public String toString(){
        final StringBuilder internal = new StringBuilder("SDR TDI(");
        internal.append(tdi);
        internal.append( ") ");
        if ( tdo != null )
        {
            internal.append( "TDO(");
            internal.append(tdo);
            internal.append( ") MASK(");
            internal.append(mask);
            internal.append( ") ");
        }
        return internal.toString();
    }
    
    /**
     * Parses a line 
     * @param line
     * @return
     * @throws ParserException 
     */
    public static SdrCommand parse(String line) throws ParserException{
        if ( line == null )
            return null;
        final StringTokenizer tok = new StringTokenizer(line.toLowerCase().trim(), " ()\t", true);
        Long tdi = null;
        Long tdo = null;
        Long mask = null;
        String token = tok.nextToken(); // the first token is the command
        
        //Checking for TDI
        try{
            do{
                token = tok.nextToken().trim();
            } while( token.length() == 0 );
            if ( !token.equalsIgnoreCase(TDI) )
                throw new ParserException("Expected TDI after SDR.");
            tdi = getArgument(tok,TDI );
        } 
        catch ( NoSuchElementException e ) 
        {
            throw new ParserException("Expected TDI after SDR.");
        }
        
        //Checking for TDO
        try{
            do{
                token = tok.nextToken().trim();
            } while( token.length() == 0 );
            if ( !token.equalsIgnoreCase(TDO) )
                throw new ParserException("Unexpected token after TDI.");
            tdo = getArgument(tok,TDO );
        } 
        catch ( NoSuchElementException e ) 
        {
            return new SdrCommand(tdi, tdo, mask);
        }  
        
        //Checking for MASK
        try{
            do{
                token = tok.nextToken().trim();
            } while( token.length() == 0 );
            if ( !token.equalsIgnoreCase(MASK) )
                throw new ParserException("Unexpected token after TDO.");
            mask = getArgument(tok,MASK );
        } 
        catch ( NoSuchElementException e ) 
        {
            mask = Long.MAX_VALUE;
            return new SdrCommand(tdi, tdo, mask);
        }  
        
        if ( tok.hasMoreTokens() )
        {
            StringBuilder extra = new StringBuilder();
            while ( tok.hasMoreTokens() )
            {
                String e = tok.nextToken().trim();
                if ( e.length() > 0 )
                {
                    extra.append(e);
                    extra.append(' ');
                }
            }
            throw new ParserException("Unexpected tokens after parsing MASK :\n"+extra);
        }
        return new SdrCommand(tdi, tdo, mask);        

        
    }



}
