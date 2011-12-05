package com.doublecheck.bstworkbench.compiler.commands;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.CompilerException;
import com.doublecheck.bstworkbench.compiler.Instruction;

public class SdrCommand extends Command {
        
    protected final static String KEYWORD = "sdr";
    
    protected final static String TDI = "tdi";
    protected final static String TDO = "tdo";
    protected final static String MASK = "mask";

    protected final byte numberBits;
    protected final Long tdi;
    protected final Long tdo;
    protected final Long mask;
    
    
    public SdrCommand(final byte numberBits , final Long tdi, final Long tdo, final Long mask) {
        this.numberBits = numberBits;
        this.tdi = tdi;
        this.tdo = tdo;
        this.mask = mask;
    }

    public String toString(){
        return toString(KEYWORD, numberBits, tdi, tdo, mask);
    }
    
    protected static String toString( final String type , final byte numberBytes , final Long tdi, final Long tdo, final Long mask){
        final StringBuilder internal = new StringBuilder(type);
        internal.append(' ');
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
     * @throws CompilerException 
     */
    public static SdrCommand parse(Token tok) throws CompilerException{
       return SdrCommand.parseLine(tok, KEYWORD);        
    }

    protected static SdrCommand parseLine(Token tok , final String type ) throws CompilerException{
        if ( tok == null )
            return null;
        byte numberBytes = 0;
        Long tdi = null;
        Long tdo = null;
        Long mask = null;
        
        tok = tok.getNextToken(); // first should be a whitespace
        if ( tok == null || tok.type == Token.NULL )
            throw new CompilerException("Expected numeric argument after " + type + ".");
        
        try{
            tok = tok.getNextToken(); // byte count
            if ( tok == null || tok.type != Token.LITERAL_NUMBER_HEXADECIMAL )
                throw new CompilerException("Expected numeric argument after " + type + ".");
            numberBytes = Byte.parseByte(tok.getLexeme());
            mask = getMaximumNumber(numberBytes);
        } 
        catch ( NumberFormatException e ) 
        {
            throw new CompilerException("Error parsing the numeric argument (" + tok.getLexeme() +") after " + type + ".");
        }
        
        tok = tok.getNextToken(); // should be a whitespace
        if ( tok == null || tok.type == Token.NULL )
            throw new CompilerException("Expected TDI after " + type + ".");
        
        
        //Checking for TDI
        tok = tok.getNextToken(); // byte count
        if ( !tok.getLexeme().equalsIgnoreCase(TDI) )
            throw new CompilerException("Expected TDI after " + type + ".");
        ArgumentParserResult args = getArgument(tok,TDI );
        tdi = args.argument;
        tok = args.token;
        
        
        tok = tok.getNextToken();  // should be a whitespace
        if ( tok == null || tok.type == Token.NULL  )
            return new SirCommand(numberBytes,tdi, tdo, mask);
        
        
        //Checking for TDO ( optional )
        tok = tok.getNextToken(); // byte count
        if ( tok == null || tok.type == Token.NULL  )
            return new SirCommand(numberBytes,tdi, tdo, mask);
        if ( !tok.getLexeme().equalsIgnoreCase(TDO) )
            throw new CompilerException("Unexpected token after TDI.");
        args = getArgument(tok,TDO );
        tdo = args.argument;
        tok = args.token;
        
        tok = tok.getNextToken(); // should be a whitespace
        if ( tok == null || tok.type == Token.NULL  )
            return new SirCommand(numberBytes,tdi, tdo, mask);
        
        //Checking for MASK( optional )
        tok = tok.getNextToken(); // byte count
        if ( tok == null || tok.type == Token.NULL  )
            return new SirCommand(numberBytes,tdi, tdo, mask);

        if ( !tok.getLexeme().equalsIgnoreCase(MASK) )
            throw new CompilerException("Unexpected token after TDO.");
        args = getArgument(tok,MASK );
        mask = args.argument;
        tok = args.token;
 
        //Check for extra 
        String extra = getLineEnd(tok);
        if ( extra.length() > 0 )
            throw new CompilerException("Unexpected tokens after parsing MASK:\n"+extra.toString());
        return new SirCommand(numberBytes,tdi, tdo, mask);        
    }

    @Override
    public void checkConsistency() throws CompilerException {
        if ( numberBits == 0 )
            throw new CompilerException("Number of bytes cannot be zero.");
        final Long maxNumber = getMaximumNumber(numberBits);
        if ( tdi > maxNumber )
            throw new CompilerException("TDI cannot fit in the number of bytes.");
        if ( tdo != null ){
            if ( tdo > maxNumber )
                throw new CompilerException("TDO cannot fit in the number of bytes.");
            if ( mask > maxNumber )
                throw new CompilerException("MASK cannot fit in the number of bytes.");
        }
        
    }

    @Override
    public List<Instruction> getInstruction() {
        TapStateMachine stateMachine = TapStateMachine.getInstance();
        List<Instruction> ret = new ArrayList<Instruction>();
        ret.addAll(stateMachine.moveToState("shift-dr"));
        int numberBytes = numberBits/8;
        if ( numberBytes*8 < numberBits )
            numberBytes++;
        ret.add(new Instruction(Command.TDI, numberBytes, tdi));
        if ( tdo != null )
        {
            ret.add(new Instruction(Command.TDO, numberBytes, tdi));
            ret.add(new Instruction(Command.MASK, numberBytes, mask));

        }
        return ret;
    }

}
