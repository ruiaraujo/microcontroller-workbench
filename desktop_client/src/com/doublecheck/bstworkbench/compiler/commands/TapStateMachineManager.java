package com.doublecheck.bstworkbench.compiler.commands;

import java.util.HashMap;
import java.util.Map;


public class TapStateMachineManager {

    private final Map<Long, TapStateMachine> registeredTapControllers ;
    private Long currentTap;
    /**
     * SingletonHolder is loaded on the first execution of
     * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
     * not before.
     */
    private static class TapStateMachineHolder {
        public static final TapStateMachineManager instance = new TapStateMachineManager();
    }

    /**
     * @return
     */
    public static TapStateMachineManager getInstance() {
        return TapStateMachineHolder.instance;
    }

    private TapStateMachineManager() {
    	registeredTapControllers = new HashMap<Long, TapStateMachine>();
    	currentTap = null;
    }

    public void selectNewTap( Long tapNumber ){
    	if ( currentTap == tapNumber )
    		return;
    	currentTap = tapNumber;
    	if ( !registeredTapControllers.containsKey(currentTap) )
    		registeredTapControllers.put(currentTap, new TapStateMachine());
    }
    
    public TapStateMachine getCurrentTapStateMachine(){
    	return registeredTapControllers.get(currentTap);
    }

    public TapStateMachine getTapStateMachine(Long tapNumber){
    	selectNewTap(tapNumber);
    	return registeredTapControllers.get(currentTap);
    }
   
}
