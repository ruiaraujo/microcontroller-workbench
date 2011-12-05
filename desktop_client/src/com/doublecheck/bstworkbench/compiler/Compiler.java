package com.doublecheck.bstworkbench.compiler;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.Token;

import com.doublecheck.bstworkbench.compiler.commands.Command;
import com.doublecheck.bstworkbench.compiler.commands.SupportedOperations;
import com.doublecheck.bstworkbench.compiler.commands.TapStateMachine;

public class Compiler {
    private final List<Error> errors;
    private final List<Command> commands;
    private final List<Instruction> instructions;
    
    public Compiler(){
        errors = new ArrayList<Error>();
        commands = new ArrayList<Command>();
        instructions = new ArrayList<Instruction>();
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
                        {  
                            com.checkConsistency();
                            instructions.addAll(com.getInstructions());
                            commands.add(com);
                        }
                        else
                            addError(i+1,"Unknown identifier: " + new String(tok.text));
                    } catch (CompilerException e) {
                        addError(i+1,e.getMessage());
                    }
                    break;
                }
                default: 
                    addError(i+1,"Unknown identifier: " + new String(tok.text));
                    break;
            }
        }
        if ( !detectedErrors() )
            instructions.addAll(0,TapStateMachine.getResetInstructions()); 
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

    public List<Instruction> getInstructions() {
        return instructions;
    }
}
