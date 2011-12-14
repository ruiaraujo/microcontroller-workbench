package com.doublecheck.bstworkbench.io.rs232;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import com.doublecheck.bstworkbench.io.MicrocontrollerManager;

public class SerialManager implements MicrocontrollerManager{
	   private CommPortIdentifier portId;
	   private Enumeration portList;
	   private Listener listener;
	    InputStream inputStream;
	    OutputStream outputStream;
	    SerialPort serialPort;

	    public SerialManager() {
	        portList = CommPortIdentifier.getPortIdentifiers();

	        while (portList.hasMoreElements()) {
	            portId = (CommPortIdentifier) portList.nextElement();
	            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	                System.out.println("serial " + portId.getName() );
	                break;
	                // if (portId.getName().equals("COM9")) {
	                //if (portId.getName().equals("/dev/ttyUSB0")) {
	                //}
	            }
	        }
	        if ( portId == null )
	        	return;
	        try {
	            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
	        } catch (PortInUseException e) {System.out.println(e);}
	        try {
	            inputStream = serialPort.getInputStream();

	            outputStream = serialPort.getOutputStream();
	        } catch (IOException e) {System.out.println(e);}
		try {
			listener = new Listener();
		} catch (TooManyListenersException e) {System.out.println(e);}
	        serialPort.notifyOnDataAvailable(true);
	        try {
	            serialPort.setSerialPortParams(9600,
	                SerialPort.DATABITS_8,
	                SerialPort.STOPBITS_1,
	                SerialPort.PARITY_NONE);
	        } catch (UnsupportedCommOperationException e) {System.out.println(e);}
	        new Thread(listener).start();
	    }
	    
	    public void initProgram(){
	    	try {
	    		byte p = 0x70;
		    	outputStream.write(p);
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void write(byte[] b){
	    	try {
		    	outputStream.write(b);
		    	outputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    public void finishProgram(){
	    	try {
		    	outputStream.write('f');
		    	outputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void runProgram(){
	    	try {
		    	outputStream.write('r');
		    	outputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    private class Listener implements  Runnable ,  SerialPortEventListener {
	    	
	    	private Listener() throws TooManyListenersException{
		        serialPort.addEventListener(this);	
	    	}
	    	
			public void run() {
				while (true)
					{
					try {
					
						Thread.sleep(10000000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		    public void serialEvent(SerialPortEvent event) {
		        switch(event.getEventType()) {
		        case SerialPortEvent.BI:
		        case SerialPortEvent.OE:
		        case SerialPortEvent.FE:
		        case SerialPortEvent.PE:
		        case SerialPortEvent.CD:
		        case SerialPortEvent.CTS:
		        case SerialPortEvent.DSR:
		        case SerialPortEvent.RI:
		        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
		            break;
		        case SerialPortEvent.DATA_AVAILABLE:
		            byte[] readBuffer = new byte[2048];

		            try {
		            	int numBytes = 0;
		                while (inputStream.available() > 0 ) {
		                    numBytes += inputStream.read(readBuffer,numBytes,readBuffer.length-numBytes );
		                    if ( numBytes == readBuffer.length )
		                    	break;
		                   // Thread.sleep(100);
		                }
		                System.out.println(new String(readBuffer));
		            } catch (IOException e) {System.out.println(e);} /*catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
		            break;
		        }
		    }
		}

		public void write(byte b) {
	    	try {
		    	outputStream.write(b);
		    	outputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}

	
}
