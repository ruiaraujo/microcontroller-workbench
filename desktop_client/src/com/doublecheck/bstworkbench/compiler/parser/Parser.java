package com.doublecheck.bstworkbench.compiler.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.doublecheck.bstworkbench.compiler.commands.Command;

public class Parser {
    private final List<Error> errors;
    private final List<Command> commands;
    public Parser(){
        errors = new ArrayList<Error>();
        commands = new ArrayList<Command>();
    }
    
    public void parse(String text){
        final  StringTokenizer tok = new StringTokenizer(text.toLowerCase(), "\n\r", true);
        int line = 1;
        errors.clear();
        while ( tok.hasMoreTokens() )
        {
            String token = tok.nextToken().trim();
            if ( token.length() != 0 )
            {
                                //parseLine(token, line);
                try {
                    Command  com = SupportedOperations.parseLine(token);
                    if ( com != null )
                        commands.add(com);
                    else
                        addError(line,"Unknown identifier: " + token);
                } catch (ParserException e) {
                    addError(line,e.getMessage());
                }
            }
            ++line;
        }
            
    }
 
    
    public boolean detectedErrors() {
        return !errors.isEmpty();
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

    public List<Command> getCommands() {
        return commands;
    }
}
