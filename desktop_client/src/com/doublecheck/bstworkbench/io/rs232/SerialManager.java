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


import com.doublecheck.bstworkbench.StringUtils;
import com.doublecheck.bstworkbench.io.MicrocontrollerManager;

public class SerialManager implements MicrocontrollerManager {

	private State state;
	private enum State {
		RUNNING, PAUSED, TDO, TDI,  DEBUG, UPLOADING, READING_SIZE , TDO_READ ;
	};
	
    private class SerialInputListener implements Runnable,
            SerialPortEventListener {
    	
    	private int numberBytesTdi;
    	private int numberBytesTdo;
    	private byte [] tdi;
    	private byte [] tdo;
    	private byte [] tdoError;
    	private boolean hadError = false;
    	private boolean doNotWriteOnError = false;
        private boolean stop = false;

        private SerialInputListener() throws TooManyListenersException {
            serialPort.addEventListener(this);
        }

        public void run() {
            while (true) {
                try {
                    if (stop)
                        return;
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void serialEvent(final SerialPortEvent event) {
            switch (event.getEventType()) {
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
                try {
                    final byte[] readBuffer = new byte[inputStream.available() + 2048];
                    int numBytes = 0;
                    while (inputStream.available() > 0) {
                        numBytes += inputStream.read(readBuffer, numBytes,
                                readBuffer.length - numBytes);
                        if (numBytes == readBuffer.length) {
                            break;
                        }
                        Thread.sleep(100);
                    }
                    for ( int i = 0 ; i < numBytes ; ++i )
                    {
                    	switch( state ){
                        	case PAUSED: break;
                        	case RUNNING:{
                        		if ( readBuffer[i] == 'a' )
                        			listeners.onAckReceived();
                        		else if ( readBuffer[i] == 's' )
                        			state = State.READING_SIZE;
                        		else if ( readBuffer[i] == 't' )
                        			state = State.TDI;
                        		else if ( readBuffer[i] == 'f' )
                        			state = State.TDO;
                        		else if ( readBuffer[i] == 'x' )
                        			state = State.TDO_READ;
                        		else if ( readBuffer[i] == 'e' )
                        		{
                        			hadError = true;
                        			state = State.TDO_READ;
                        		}
                        		break;
                        	}
                        	case UPLOADING:{
                        		if ( readBuffer[i] == 'o' )
                        			listeners.onOutOfMemory();
                        		break;
                        	}
                        	case DEBUG:System.out.print(StringUtils.getHexString(readBuffer[i]));break;
                        	case READING_SIZE: {
                        		numberBytesTdo = numberBytesTdi = readBuffer[i];
                        		tdi = new byte[numberBytesTdi];
                        		tdo = new byte[numberBytesTdo];
                        		tdoError = new byte[numberBytesTdo];
                        		state = State.RUNNING; break;
                        	}
                        	case TDI:{
                        		tdi[numberBytesTdi-1] = readBuffer[i];
                        		--numberBytesTdi;
                        		if ( numberBytesTdi == 0 )
                        		{
                        			listeners.onTDISent(StringUtils.getHexString(tdi));
                        		}
                        		state = State.RUNNING;
                        		break;
                        	}
                        	case TDO:{
                        		tdo[numberBytesTdo-1] = readBuffer[i];
                        		--numberBytesTdo;
                        		if ( numberBytesTdo == 0)
                        		{
                        			if ( hadError )
                        			{
                        				listeners.onErrorAckReceived(StringUtils.getHexString(tdoError),
       										 StringUtils.getHexString(tdo));
                        				hadError = false;
                                		state = State.RUNNING;
                        				return;
                        			}
                        			else
                        				listeners.onTDOReceived(StringUtils.getHexString(tdo));
                        			
                        		}
                        		state = State.RUNNING;
                        		break;
                        	}
                        	case TDO_READ:{
                        		tdoError[numberBytesTdo-1] = readBuffer[i];
                        		state = State.RUNNING;
                        		break;
                        	}
                        		
                        	
                        }
                    }
                    /*String input = new String(readBuffer);
                    if ( state == State.RUNNING )
                    {
                    	int pos = 0, readBufferPos  = 0;
                    	boolean error = false;
                    	while ( ( pos = input.indexOf('e') ) != -1){
                    		error = true;
                    		System.out.print(input.substring(0,pos+1));
                    		for ( int i = pos +1 + readBufferPos  ;  i < pos +4 +readBufferPos; ++i )
                    			System.out.print(StringUtils.getHexString(readBuffer[i]) + ' ');
                    		input = input.substring(pos+4);
                    		readBufferPos += pos+4;
                    	}
                    	System.out.println(input);
                    }
                    else
                    	if ( state == State.DEBUG )
                    	{
                    		for ( int i = 0  ;  i < readBuffer.length; ++i )
                    			System.out.print(StringUtils.getHexString(readBuffer[i]));

                    	}
                    	else	
                    	System.out.println(input);*/
                } catch (final IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
					e.printStackTrace();
				}
                break;
            }
        }
    }

    private SerialInputListener listener;
    private InputStream inputStream;
    private OutputStream outputStream;
    private SerialPort serialPort;

    private boolean connected;

    public SerialManager() {
        connected = false;
        state = State.PAUSED;
    }

    @SuppressWarnings("unchecked")
    public boolean connect(final String name) {
        if (connected)
            return true;
        final Enumeration<CommPortIdentifier> portList = CommPortIdentifier
                .getPortIdentifiers();
        CommPortIdentifier portId = null;
        while (portList.hasMoreElements()) {
            portId = portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.println("serial " + portId.getName());
                if (portId.getName().equals(name)) {
                    break;
                }
            }
        }

        if (portId == null)
            return false;
        try {
            serialPort = portId.open("SimpleReadApp", 2000);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            listener = new SerialInputListener();
            serialPort.notifyOnDataAvailable(true);
            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (final PortInUseException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final TooManyListenersException e) {
            e.printStackTrace();
        } catch (final UnsupportedCommOperationException e) {
            e.printStackTrace();
        }

        new Thread(listener).start();
        connected = true;
        return true;
    }

    @Override
    public void disconnect() {
        try {
            listener.stop = true;
            connected = false;
            outputStream.close();
            inputStream.close();
            serialPort.close();
            state = State.PAUSED;
        } catch (final IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void finishProgram() {
        if (!connected)
            return;
        state = State.PAUSED;
        try {
            outputStream.write('f');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void initProgramming() {
        if (!connected)
            return;
        state = State.UPLOADING;
        try {
            outputStream.write('p');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void runProgram() {
        if (!connected)
            return;
        try {
        	state = State.RUNNING;
            outputStream.write('r');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public void write(final byte b) {
        if (!connected)
            return;
        state = State.UPLOADING;
        try {
            outputStream.write(b);
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public void write(final byte[] b) {
        if (!connected)
            return;
        state = State.UPLOADING;
        try {
            outputStream.write(b);
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void stopProgram() {
        if (!connected)
            return;
        state = State.PAUSED;
        try {
            outputStream.write('s');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
            disconnect();
        }
        
    }
    

    public void debugProgram() {
        if (!connected)
            return;
        state = State.DEBUG;
        try {
            outputStream.write('l');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
            disconnect();
        }
        
    }


    @Override
    public void stepProgram() {
        if (!connected)
            return;
        try {
        	state = State.RUNNING;
            outputStream.write('t');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    AcknowledgementListener listeners = null;
    
	public void addAcknowledgementListener(AcknowledgementListener listener) {
		if ( listeners == null )
		{
			listeners = listener;
		}
		else 
			throw new RuntimeException("Too many listeners!! :P");
	}

	public void removeAcknowledgementListener(AcknowledgementListener listener) {
		listeners = null;
	}
}
