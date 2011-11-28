package com.doublecheck.bstworkbench.compiler.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Parser {
    private final List<Error> errors;
    
    public Parser(){
        errors = new ArrayList<Error>();
    }
    
    public void parse(String text){
        final  StringTokenizer tok = new StringTokenizer(text, "\n\r", true);
        int line = 1;
        errors.clear();
        while ( tok.hasMoreTokens() )
        {
            String token = tok.nextToken().trim();
            if ( token.length() != 0 )
                parseLine(token, line);
            ++line;
        }
            
    }
    private void parseLine(String line , int lineNumber){
        final String[] tokens = tokenizer(new StringTokenizer(line, " \t"));
     
        //Parsing SETAP
        if ( SupportedOperations.SELTAP.equalsIgnoreCase(tokens[0]) )
        {
            if ( tokens.length >= 2  )
            {
                String num = tokens[1];
                try{
                    Integer.parseInt(num);
                } catch(Exception e){
                    addError(lineNumber,"Error parsing " + tokens[0] + " argument: " + num);
                    return;
                }
                //parseLineTrash(, lineNumber, num);
                return;
            }
            else
            {
                addError(lineNumber,"Expected numeric argument after parsing " + tokens[0]);
                return;
            }
            
        }
        //Parsing TMS
        if ( SupportedOperations.TMS.equalsIgnoreCase(tokens[0]) )
        {
            if ( tokens.length >= 1 )
            {
                String num = tokens[1].trim();
                try{
                    Integer.parseInt(num);
                } catch(Exception e){
                    addError(lineNumber,"Error parsing " + tokens[0] + " argument: " + num);
                    return;
                }
                //parseLineTrash(tok, lineNumber, num);
                return;
            }
            else
            {
                addError(lineNumber,"Expected numeric argument after parsing " + tokens[0]);
                return;
            }
            
        }
        //PARSING STATE
        if ( SupportedOperations.STATE.equalsIgnoreCase(tokens[0]) )
        {
            if (  tokens.length >= 1 )
            {
               // String state = tok.nextToken().trim();
               // parseLineTrash(tok, lineNumber, state);
                return;
            }
            else
            {
                addError(lineNumber,"Expected argument after parsing " + tokens[0]);
                return;
            }
            
        }
        //PARSING SDR or SIR 
        if ( SupportedOperations.SDR.equalsIgnoreCase(tokens[0]) || 
        		SupportedOperations.SIR.equalsIgnoreCase(tokens[0]) )
        {
            if ( tokens.length >= 1 )
            {
               // String state = tok.nextToken().trim();
                //parseLineTrash(tok, lineNumber, state);
                return;
            }
            else
            {
                addError(lineNumber,"Expected  argument after parsing " + tokens[0]);
                return;
            }
            
        }

        addError(lineNumber,"Unknown identifier: " + tokens[0]);
        return;

    }
    
    private String [] tokenizer(StringTokenizer tok){
    	if ( tok == null )
    		return null;
    	final String [] tokens = new String[tok.countTokens()];
    	for ( int i = 0 ; i < tokens.length ; ++i  )
    		tokens[i] = tok.nextToken();
    	return tokens;
    }
    
    public boolean detectedErrors() {
        return !errors.isEmpty();
    }
    
  /*  private void parseLineTrash( final StringTokenizer tok , final int lineNumber,
            final String lastTok){
        if ( tok.hasMoreTokens() )
        {
            String extra = "";
            while ( tok.hasMoreTokens() )
                extra += tok.nextToken() + " ";
            addError(lineNumber,"Unexpected tokens after parsing " +lastTok +
                        " :\n"+extra);
        }
    }*/
    
    private void addError(int line , String error){
        errors.add(new Error(line, error));
    }
    
    public List<String> getListErrors(){
        final List<String> errorMessages = new ArrayList<String>();
        for ( Error e : errors )
            errorMessages.add("Line " + e.line + ": " + e.error);        
        return errorMessages;
    }
    
    private class Error{
        private int line;
        private String error;
        private Error(int line,String error){
            this.line = line;
            this.error = error;
        }
    }
}
