package com.doublecheck.bstworkbench.compiler.commands;

import org.fife.ui.rsyntaxtextarea.Token;

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
        final StringBuilder internal = new StringBuilder("SIR ");
        internal.append(numberBytes);
        internal.append(" TDI(");
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
    public static SirCommand parse(Token tok) throws ParserException{
        if ( tok == null )
            return null;
        byte numberBytes = 0;
        Long tdi = null;
        Long tdo = null;
        Long mask = null;
        
        tok = tok.getNextToken(); // first should be a whitespace
        if ( tok == null || tok.type == Token.NULL )
            throw new ParserException("Expected numeric argument after SIR.");
        
        try{
            tok = tok.getNextToken(); // byte count
            if ( tok == null || tok.type != Token.LITERAL_NUMBER_HEXADECIMAL )
                throw new ParserException("Expected numeric argument after SIR.");
            numberBytes = Byte.parseByte(tok.getLexeme());
        } 
        catch ( NumberFormatException e ) 
        {
            throw new ParserException("Error parsing the numeric argument (" + tok.getLexeme() +") after SIR.");
        }
        
        tok = tok.getNextToken(); // should be a whitespace
        if ( tok == null || tok.type == Token.NULL )
            throw new ParserException("Expected TDI after SIR.");
        
        
        //Checking for TDI
        tok = tok.getNextToken(); // byte count
        if ( !tok.getLexeme().equalsIgnoreCase(TDI) )
            throw new ParserException("Expected TDI after SIR.");
        ArgumentParserResult args = getArgument(tok,TDI );
        tdi = args.argument;
        tok = args.token;
        
        
        tok = tok.getNextToken();  // should be a whitespace
        if ( tok == null || tok.type == Token.NULL  )
            return new SirCommand(numberBytes,tdi, tdo, mask);
        
        
        //Checking for TDO
        tok = tok.getNextToken(); // byte count
        if ( tok == null || tok.type == Token.NULL  )
            return new SirCommand(numberBytes,tdi, tdo, mask);
        if ( !tok.getLexeme().equalsIgnoreCase(TDO) )
            throw new ParserException("Unexpected token after TDI.");
        args = getArgument(tok,TDO );
        tdo = args.argument;
        tok = args.token;
        
        //Checking for MASK
        tok = tok.getNextToken(); // byte count
        if ( tok == null || tok.type == Token.NULL  )
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
        
        //Checking for MASK
        tok = tok.getNextToken(); // byte count
        if ( tok == null || tok.type == Token.NULL  )
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
        if ( !tok.getLexeme().equalsIgnoreCase(MASK) )
            throw new ParserException("Unexpected token after TDO.");
        args = getArgument(tok,MASK );
        mask = args.argument;
        tok = args.token;
 
        //Check for extra 
        String extra = getLineEnd(tok);
        if ( extra.length() > 0 )
            throw new ParserException("Unexpected tokens after parsing MASK:\n"+extra.toString());
        return new SirCommand(numberBytes,tdi, tdo, mask);        
    }



}
