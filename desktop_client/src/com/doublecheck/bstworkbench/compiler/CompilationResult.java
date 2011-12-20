package com.doublecheck.bstworkbench.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CompilationResult {

    private final Map<Integer,List<Instruction>>instructions;
	private final List<Instruction> instructionList;
	private final List<byte[]> compiledOutput;
    public CompilationResult() {
        instructions = new TreeMap<Integer, List<Instruction>>();
        instructionList = new ArrayList<Instruction>(instructions.size());
        compiledOutput = new ArrayList<byte[]>();
	}
    
    public List<Instruction> getInstructions(){
    	return instructionList;
    }
    
    public List<Instruction> getInstructions(int line){
    	return instructions.get(line);
    }
    
    
    public void addInstructions(int line , List<Instruction> ins){
    	instructions.put(line,ins);
    	instructionList.addAll(ins);
    	for ( Instruction i : ins )
    		compiledOutput.add(i.toFile());
    }
    
    
    public List<Integer> validLines(){
    	return new ArrayList<Integer>(instructions.keySet());
    }
    
    public List<byte[]> getOutputFile(){
    	return compiledOutput;
    }
    
    
}
