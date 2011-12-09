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

public class SerialManager implements MicrocontrollerManager, SerialPortEventListener{
	   private CommPortIdentifier portId;
	   private Enumeration portList;

	    InputStream inputStream;
	    OutputStream outputStream;
	    SerialPort serialPort;

	    public SerialManager() {
	        portList = CommPortIdentifier.getPortIdentifiers();

	        while (portList.hasMoreElements()) {
	            portId = (CommPortIdentifier) portList.nextElement();
	            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	                System.out.println("serial " + portId.getName() );
	                // if (portId.getName().equals("COM9")) {
	                if (portId.getName().equals("/dev/ttyUSB0")) {
	                    SerialManager reader = new SerialManager();
	                }
	            }
	        }
	        try {
	            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
	        } catch (PortInUseException e) {System.out.println(e);}
	        try {
	            inputStream = serialPort.getInputStream();
	            outputStream = serialPort.getOutputStream();
	        } catch (IOException e) {System.out.println(e);}
		try {
	            serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {System.out.println(e);}
	        serialPort.notifyOnDataAvailable(true);
	        try {
	            serialPort.setSerialPortParams(38400,
	                SerialPort.DATABITS_8,
	                SerialPort.STOPBITS_1,
	                SerialPort.PARITY_NONE);
	        } catch (UnsupportedCommOperationException e) {System.out.println(e);}

	    }
	    
	    public void initProgram(){
	    	try {
		    	outputStream.write('p');
				outputStream.flush();
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
	            byte[] readBuffer = new byte[20];

	            try {
	                while (inputStream.available() > 0) {
	                    int numBytes = inputStream.read(readBuffer);
	                }
	                System.out.print(new String(readBuffer));
	            } catch (IOException e) {System.out.println(e);}
	            break;
	        }
	    }
}
