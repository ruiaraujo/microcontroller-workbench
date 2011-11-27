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
        final StringTokenizer tok = new StringTokenizer(line, " \t");
        final String firstTok = tok.nextToken().trim();
        if ( SupportedOperations.SELTAP.equalsIgnoreCase(firstTok) )
        {
            if ( tok.hasMoreTokens() )
            {
                String num = tok.nextToken().trim();
                try{
                    Integer.parseInt(num);
                } catch(Exception e){
                    addError(lineNumber,"Error parsing " + firstTok + " argument: " + num);
                    return;
                }
                parseLineTrash(tok, lineNumber, num);
                return;
            }
            else
            {
                addError(lineNumber,"Expected numeric argument after parsing " + firstTok);
                return;
            }
            
        }
        if ( SupportedOperations.TMS.equalsIgnoreCase(firstTok) )
        {
            if ( tok.hasMoreTokens() )
            {
                String num = tok.nextToken().trim();
                try{
                    Integer.parseInt(num);
                } catch(Exception e){
                    addError(lineNumber,"Error parsing " + firstTok + " argument: " + num);
                    return;
                }
                parseLineTrash(tok, lineNumber, num);
                return;
            }
            else
            {
                addError(lineNumber,"Expected numeric argument after parsing " + firstTok);
                return;
            }
            
        }
        if ( SupportedOperations.STATE.equalsIgnoreCase(firstTok) )
        {
            if ( tok.hasMoreTokens() )
            {
                String state = tok.nextToken().trim();
                parseLineTrash(tok, lineNumber, state);
                return;
            }
            else
            {
                addError(lineNumber,"Expected numeric argument after parsing " + firstTok);
                return;
            }
            
        }
        addError(lineNumber,"Unknown identifier: " + firstTok);
        return;

    }
    
    public boolean detectedErrors() {
        return !errors.isEmpty();
    }
    
    private void parseLineTrash( final StringTokenizer tok , final int lineNumber,
            final String lastTok){
        if ( tok.hasMoreTokens() )
        {
            String extra = "";
            while ( tok.hasMoreTokens() )
                extra += tok.nextToken() + " ";
            addError(lineNumber,"Unexpected tokens after parsing " +lastTok +
                        " :\n"+extra);
        }
    }
    
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
