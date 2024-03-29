package com.doublecheck.bstworkbench.compiler.commands;


import java.util.List;

import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.CompilerException;
import com.doublecheck.bstworkbench.compiler.Instruction;


/**
 * Abstract class which holds some common methods from
 * the commands
 * @author ruka
 *
 */
public abstract class Command {
    protected final static byte TMS0 = 0;
    protected final static byte TMS1 = 1;
    protected final static byte TDI = 2;
    protected final static byte TDO = 3;
    protected final static byte MASK = 4;
    protected final static byte SELTAP = 5;
    protected final static byte RUNTEST = 6;
    
    /**
     * After parsing correctly, the method should be invoked by the compiler
     * to check for invalid arguments.
     * @throws CompilerException
     */
    public abstract void checkConsistency() throws CompilerException;
    
    public abstract List<Instruction> getInstructions(); 
    
    /**
     * 
     * @param tok A token belonging to a token linked list
     * @param type the keyword being parsed, only here for more explicit error messages
     * @return an {@link ArgumentParserResult}'s instance with the updated token state and the parsed argument.
     * @throws CompilerException
     */
    protected static ArgumentParserResult getArgument(Token tok , final String type) throws CompilerException{
        Long ret = null;
        //Checking for '('
        tok = tok.getNextToken();
        if (  tok.type  == Token.WHITESPACE )
            tok = tok.getNextToken();
        if ( tok.type != Token.SEPARATOR )
            throw new CompilerException("Expected '(' after " + type + ".");
        
        //Checking for the argument
        tok = tok.getNextToken();
        if (  tok.type  == Token.WHITESPACE )
            tok = tok.getNextToken();
        try{
        	ret = Long.parseLong(tok.getLexeme(),16);
	    } 
	    catch ( NumberFormatException e ) 
	    {
	        throw new CompilerException("Error parsing the numeric argument (" + tok.getLexeme() +") after " + type + ".");
	    }
        
        //Checking for '('
        tok = tok.getNextToken();
        if (  tok.type  == Token.WHITESPACE )
            tok = tok.getNextToken();
        if ( tok.type != Token.SEPARATOR )
            throw new CompilerException("Expected ')' after " + type + ".");

        return new ArgumentParserResult(ret, tok);
    }
    
    /**
     * This stupid class only serves the purposes of returning the token
     * as well as the parsed argument.
     * @author Rui Araújo
     *
     */
    protected static class ArgumentParserResult{
        protected Long argument;
        protected Token token;
        protected ArgumentParserResult( final Long ret , final Token tok ){
            this.argument = ret;
            this.token = tok;
        }
    }
    
    /**
     * Parses the end of a line after a successfully parsed command for
     * stupid characters while ignoring comments and whitespace.                      
     * @param tok A token belonging to a token linked list
     * @return
     */
    protected static String getLineEnd(Token tok){
        tok =  tok.getNextToken();
        StringBuilder extra = new StringBuilder();
        while ( tok != null && tok.type != Token.NULL  )
        {
            
            if ( tok.type != Token.NULL && tok.type != Token.WHITESPACE &&  tok.type != Token.COMMENT_EOL &&  tok.type != Token.COMMENT_MULTILINE )
            {
                extra.append(tok.getLexeme());
                extra.append(' ');
            }
            tok = tok.getNextToken();
        }
        return extra.toString().trim();
    }
    
    /**
     * Returns the maximum number in decimal format for a certain number of bits.
     * @param numberBits number of bits
     * @return the maximum number for a number of bits
     */
    protected static Long getMaximumNumber( final int numberBits ){
        StringBuilder maskStr = new StringBuilder();
        for ( byte i = 0; i <numberBits; ++i   )
            maskStr.append('1');
        if ( maskStr.toString().length() == 0 )
            return 0L;
        return Long.parseLong(maskStr.toString(),2);
    }
    
    /**
     * Returns the maximum number in decimal format for a certain number of bits.
     * @param numberBits number of bits
     * @return the maximum number for a number of bits
     */
    protected static byte getNumberBytes( final long number ){
    	String longStr = Long.toBinaryString(number).replace('0', '1'); ;
    	int numberBits = longStr.length();
    	byte numberBytes = (byte) (numberBits / 8);
    	if ( numberBytes * 8 < numberBits )
    		numberBytes++;
    	return numberBytes;
    	
    }
}
