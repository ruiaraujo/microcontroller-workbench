package com.doublecheck.bstworkbench.io;

public interface MicrocontrollerManager {
    public boolean connect(final String name);

    public void disconnect();
    
    public boolean isConnected();

    public void finishProgram();

    public void initProgram();

    public void runProgram();
    
    public void stopProgram();
    
    public void stepProgram();
    
    public void write(byte[] b);
    
    public void addAcknowledgementListener( AcknowledgementListener listener);

    public void removeAcknowledgementListener( AcknowledgementListener listener);
    
    public String SERIAL_PREF_KEY = "com.doublecheck.bstworkbench.io.SERIAL_KEY";

    public interface AcknowledgementListener{
    	public void onAckReceived();
    	
    	public void onErrorAckReceived();
    }
}
