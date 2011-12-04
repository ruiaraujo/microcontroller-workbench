package com.doublecheck.bstworkbench.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.commands.Command;

public class Parser {
    private final List<Error> errors;
    private final List<Command> commands;
    public Parser(){
        errors = new ArrayList<Error>();
        commands = new ArrayList<Command>();
    }
    
    public void parse(RSyntaxDocument rSyntaxDocument){
        int size = rSyntaxDocument.getLineCount();
        for ( int i = 0 ; i < size ; ++i  )
        {
            Token tok = rSyntaxDocument.getTokenListForLine(i);
            if ( tok.type == Token.NULL )
                continue;
            if ( tok.type == Token.WHITESPACE )
                tok = tok.getNextToken();
            if ( tok.type == Token.COMMENT_MULTILINE )
            {
                while ( tok != null && tok.type == Token.COMMENT_MULTILINE )
                    tok = tok.getNextToken();
                if ( tok == null )
                    continue;
            }
            switch ( tok.type ){
                case Token.COMMENT_EOL:  
                case Token.NULL: 
                case Token.WHITESPACE: break;
                case Token.RESERVED_WORD:    {
                    try {
                        Command  com = SupportedOperations.parseLine(tok);
                        if ( com != null )
                            commands.add(com);
                        else
                            addError(i+1,"Unknown identifier: " + new String(tok.text));
                    } catch (ParserException e) {
                        addError(i+1,e.getMessage());
                    }
                    break;
                }
                default: 
                    addError(i+1,"Unknown identifier: " + new String(tok.text));
                    break;
            }
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
