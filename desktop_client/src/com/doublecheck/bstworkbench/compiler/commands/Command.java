package com.doublecheck.bstworkbench.compiler.commands;


import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;


/**
 * Abastract class which holds some common methods from
 * the commands
 * @author ruka
 *
 */
public abstract class Command {
    
    protected final static byte TMS = 1;
    protected final static byte TDI = 2;
    protected final static byte TDO = 3;
    protected final static byte MASK = 4;
    protected final static byte SELTAP = 5;
    
    
    protected static ArgumentParserResult getArgument(Token tok , final String type) throws ParserException{
        Long ret = null;
        //Checking for '('
        tok = tok.getNextToken();
        if (  tok.type  == Token.WHITESPACE )
            tok = tok.getNextToken();
        if ( tok.type != Token.SEPARATOR )
            throw new ParserException("Expected '(' after " + type + ".");
        
        //Checking for the argument
        tok = tok.getNextToken();
        if (  tok.type  == Token.WHITESPACE )
            tok = tok.getNextToken();
        ret = Long.parseLong(tok.getLexeme(),16);

        
        //Checking for '('
        tok = tok.getNextToken();
        if (  tok.type  == Token.WHITESPACE )
            tok = tok.getNextToken();
        if ( tok.type != Token.SEPARATOR )
            throw new ParserException("Expected ')' after " + type + ".");

        return new ArgumentParserResult(ret, tok);
    }
    
    /**
     * This stupid class only serves the purposes of returning the token
     * as well as the parsed argument.
     * @author ruka
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
    
    protected static Long getMaximumNumber( int numberBytes ){
        int numberFFs =  numberBytes/4;
        if ( numberFFs*4 < numberBytes )
            numberFFs++;
        StringBuilder maskStr = new StringBuilder();
        for ( byte i = 0; i <numberFFs; ++i   )
            maskStr.append('F');
        return Long.parseLong(maskStr.toString(),16);
    }
}
