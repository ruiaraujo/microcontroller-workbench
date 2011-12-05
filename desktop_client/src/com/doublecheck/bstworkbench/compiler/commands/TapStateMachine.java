package com.doublecheck.bstworkbench.compiler.commands;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.doublecheck.bstworkbench.compiler.Instruction;

public class TapStateMachine {
    
    
    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
     private static class TapStateMachineHolder { 
         public static final TapStateMachine instance = new TapStateMachine();
     }
      
      /**
      * @return
      */
     public static TapStateMachine getInstance() {
          return TapStateMachineHolder.instance;
     }
        
    private Node currentNode;
    public final static Map<String,Node> possibleStates = new HashMap<String,Node>();
    private TapStateMachine(){
        final Node reset = new Node("reset");
        final Node idle = new Node("idle");
        final Node select_dr = new Node("select-dr");
        final Node capture_dr = new Node("capture-dr");
        final Node shift_dr = new Node("shift-dr");
        final Node exit1_dr = new Node("exit1-dr");
        final Node pause_dr = new Node("pause-dr");
        final Node exit2_dr = new Node("exit2-dr");
        final Node update_dr = new Node("update-dr");  
        final Node select_ir = new Node("select-ir");
        final Node capture_ir = new Node("capture-ir");
        final Node shift_ir = new Node("shift-ir");
        final Node exit1_ir = new Node("exit1-ir");
        final Node pause_ir = new Node("pause-ir");
        final Node exit2_ir = new Node("exit2-ir");
        final Node update_ir = new Node("update-ir");
        currentNode = reset;//we assume that the state machine starts in the reset state
        reset.tms0 = idle;
        reset.tms1 = null;
        idle.tms0 = null;
        idle.tms1 = select_dr;
        
        //Data Register Branch
        select_dr.tms0 = capture_dr;
        select_dr.tms1 = select_ir;
        capture_dr.tms0 = shift_dr;
        capture_dr.tms1 = exit1_dr;
        shift_dr.tms0 = null;
        shift_dr.tms1 = exit1_dr;
        exit1_dr.tms0 = pause_dr;
        exit1_dr.tms1 = update_dr;
        pause_dr.tms0 = null;
        pause_dr.tms1 = exit2_dr;
        exit2_dr.tms0 = shift_dr;
        exit2_dr.tms1 = update_dr;
        update_dr.tms0 = idle;
        update_dr.tms1 = select_dr;
        
        //Instruction Register Branch
        select_ir.tms0 = capture_ir;
        select_ir.tms1 = reset;
        capture_ir.tms0 = shift_ir;
        capture_ir.tms1 = exit1_ir;
        shift_ir.tms0 = null;
        shift_ir.tms1 = exit1_ir;
        exit1_ir.tms0 = pause_ir;
        exit1_ir.tms1 = update_ir;
        pause_ir.tms0 = null;
        pause_ir.tms1 = exit2_ir;
        exit2_ir.tms0 = shift_ir;
        exit2_ir.tms1 = update_ir;
        update_ir.tms0 = idle;
        update_ir.tms1 = select_dr;
        
        possibleStates.put(reset.name, reset);
        possibleStates.put(idle.name, idle);
        
        possibleStates.put(select_dr.name, select_dr);
        possibleStates.put(capture_dr.name, capture_dr);
        possibleStates.put(shift_dr.name, shift_dr);
        possibleStates.put(exit1_dr.name, exit1_dr);
        possibleStates.put(pause_dr.name, pause_dr);
        possibleStates.put(exit2_dr.name, exit2_dr);     
        possibleStates.put(update_dr.name, update_dr);
        
        possibleStates.put(select_ir.name, select_ir);
        possibleStates.put(capture_ir.name, capture_ir);
        possibleStates.put(shift_ir.name, shift_ir);
        possibleStates.put(exit1_ir.name, exit1_ir);
        possibleStates.put(pause_ir.name, pause_ir);
        possibleStates.put(exit2_ir.name, exit2_ir);     
        possibleStates.put(update_ir.name, update_ir);
    }
    
    public List<Instruction> moveToState(final String state ){
        final Node dest = possibleStates.get(state);
        if (dest == null)
            return null;
        if ( currentNode == dest )
            return new ArrayList<Instruction>();//return empty list
        cleanNodeState();
        List<Instruction> list =  new ArrayList<Instruction>();
        if ( findNode(currentNode,dest,list) )
        {
            currentNode = dest;
            return list;
        }
        return null;
    }
    
    private boolean findNode( Node source , Node dest, List<Instruction> arrayList ){
        if ( source == null || dest == null)
            return false;
        if ( source == dest )
            return true;//return empty list
        if ( source.visited )
            return false;//already been here
        source.visited = true;
        List<Instruction> right =  new ArrayList<Instruction>();
        right.add( TmsCommand.TMS_1);
        boolean foundRight = findNode( source.tms1 , dest , right);
        
        List<Instruction> left =  new ArrayList<Instruction>();
        left.add(TmsCommand.TMS_0);
        boolean foundLeft = findNode( source.tms0 , dest , left );
        if ( foundLeft || foundRight )
        {
            if (  foundLeft &&  foundRight )
            {
                if ( left.size() < right.size() )
                    arrayList.addAll(left);
                else
                    arrayList.addAll(right);
            }
            else if ( foundLeft ){
                arrayList.addAll(left);
            }
            else
                arrayList.addAll(right);
            return true;
        }
        return false;
    }
    
    public static boolean isStateAllowed(final String state){
        if ( possibleStates.size() == 0 )
        {
            TapStateMachine.getInstance();
        }
        if (state == null  )
            return false;
        final Node dest = possibleStates.get(state);
        if (dest == null)
            return false;
        return true;
    }   
    
    
    public static List<String> getAllowedStates(){
        if ( possibleStates.size() == 0 )
        {
            TapStateMachine.getInstance();
        }
        List<String> ret = new ArrayList<String>();
        ret.addAll(possibleStates.keySet());
        return ret;
    }
    
    
    public static List<Instruction> getResetInstructions(){
        List<Instruction> ret = new ArrayList<Instruction>();
        ret.add(TmsCommand.TMS_1);
        ret.add(TmsCommand.TMS_1);
        ret.add(TmsCommand.TMS_1);
        ret.add(TmsCommand.TMS_1);
        ret.add(TmsCommand.TMS_1);
        return ret;
    }
    
    private static void cleanNodeState(){
        for ( String name :  possibleStates.keySet() )
            possibleStates.get(name).visited = false;
    }
    
    private static class Node{
        private final String name;
        private Node tms0 = null;
        private Node tms1 = null;
        private boolean visited = false;
        private Node(String name) {
            super();
            this.name = name;
        }
        
    }
}
