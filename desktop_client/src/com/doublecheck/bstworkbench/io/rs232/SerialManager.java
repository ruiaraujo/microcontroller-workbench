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

public class SerialManager implements MicrocontrollerManager {

    private class SerialInputListener implements Runnable,
            SerialPortEventListener {
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
                    }
                    System.out.println(new String(readBuffer));
                } catch (final IOException e) {
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
            outputStream.close();
            inputStream.close();
            listener.stop = true;
            connected = false;
            serialPort.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finishProgram() {
        if (!connected)
            return;
        try {
            outputStream.write('f');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initProgram() {
        if (!connected)
            return;
        try {
            outputStream.write('p');
            outputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runProgram() {
        if (!connected)
            return;
        try {
            outputStream.write('r');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void write(final byte b) {
        if (!connected)
            return;
        try {
            outputStream.write(b);
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(final byte[] b) {
        if (!connected)
            return;
        try {
            outputStream.write(b);
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
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
        try {
            outputStream.write('s');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void stepProgram() {
        if (!connected)
            return;
        try {
            outputStream.write('t');
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
