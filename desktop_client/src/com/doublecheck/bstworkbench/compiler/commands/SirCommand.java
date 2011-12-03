package com.doublecheck.bstworkbench.compiler.commands;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;

public class SirCommand extends Command {
    
    private final static byte sirIdentifier = 5;
    
    protected final static String TDI = "tdi";
    protected final static String TDO = "tdo";
    protected final static String MASK = "mask";
    
    protected final byte numberBytes;
    protected final Long tdi;
    protected final Long tdo;
    protected final Long mask;
    
    
    public SirCommand(final byte numberBytes , final Long tdi, final Long tdo, final Long mask) {
        super(sirIdentifier);
        this.numberBytes = numberBytes;
        this.tdi = tdi;
        this.tdo = tdo;
        this.mask = mask;
    }

    public String toString(){
        final StringBuilder internal = new StringBuilder("SIR TDI(");
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
    public static SirCommand parse(String line) throws ParserException{
        if ( line == null )
            return null;
        final StringTokenizer tok = new StringTokenizer(line.toLowerCase().trim(), " ()\t", true);
        byte numberBytes = 0;
        Long tdi = null;
        Long tdo = null;
        Long mask = null;
        String token = null; 
        do{
            token = tok.nextToken().trim();
        } while( token.length() == 0 );// the first token is the command
        
        do{
            token = tok.nextToken().trim();
        } while( token.length() == 0 );
        
        try{
            numberBytes = Byte.parseByte(token);
        } 
        catch ( NumberFormatException e ) 
        {
            throw new ParserException("Error parsing the numeric argument (" + token +") after SIR.");
        }
        
        //Checking for TDI
        try{
            do{
                token = tok.nextToken().trim();
            } while( token.length() == 0 );
            if ( !token.equalsIgnoreCase(TDI) )
                throw new ParserException("Expected TDI after SIR.");
            tdi = getArgument(tok,TDI );
        } 
        catch ( NoSuchElementException e ) 
        {
            throw new ParserException("Expected TDI after SIR.");
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
            return new SirCommand(numberBytes,tdi, tdo, mask);
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
            byte numberFFs = (byte) (numberBytes/4);
            if ( numberFFs*4 < numberBytes )
                numberFFs++;
            StringBuilder maskStr = new StringBuilder();
            for ( byte i = 0; i <numberFFs; ++i   )
                maskStr.append('F');
            mask = Long.parseLong(maskStr.toString(),16);
            return new SirCommand(numberBytes,tdi, tdo, mask);
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
        return new SirCommand(numberBytes,tdi, tdo, mask);        

        
    }



}
