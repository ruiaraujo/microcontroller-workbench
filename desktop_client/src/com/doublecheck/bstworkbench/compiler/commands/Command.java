package com.doublecheck.bstworkbench.compiler.commands;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.doublecheck.bstworkbench.compiler.parser.ParserException;


/**
 * Abastract class which holds some common methods from
 * the commands
 * @author ruka
 *
 */
public abstract class Command {
    
    private final byte identifier;
    public Command( final byte identifier){
        this.identifier = identifier;
    }
    public byte getIdentifier() {
        return identifier;
    }
    
    
    protected static Long getArgument(final StringTokenizer tok , final String type) throws ParserException{
        String token = null;
        Long ret = null;
        //Checking for '('
        try{
            do{
                token = tok.nextToken().trim();
            } while( token.length() == 0 );
            if ( !token.equalsIgnoreCase("(") )
                throw new ParserException("Expected '(' after " + type + ".");
        } 
        catch ( NoSuchElementException e ) 
        {
            throw new ParserException("Expected '(' after " + type + ".");
        }
        
        //Checking for the argument
        try{
            do{
                token = tok.nextToken().trim();
            } while( token.length() == 0 );
            ret = Long.parseLong(token,16);
        } 
        catch ( NoSuchElementException e ) 
        {
            throw new ParserException("Expected numeric argument after " + type + ".");
        }
        catch ( NumberFormatException e ) 
        {
            throw new ParserException("Error parsing the numeric argument (" + token +") from TDI.");
        }
        
        //Checking for '('
        try{
            do{
                token = tok.nextToken().trim();
            } while( token.length() == 0 );
            if ( !token.equalsIgnoreCase(")") )
                throw new ParserException("Expected ')' after " + type + ".");
        } 
        catch ( NoSuchElementException e ) 
        {
            throw new ParserException("Expected ')' after " + type + ".");
        }
        return ret;
    }

}
