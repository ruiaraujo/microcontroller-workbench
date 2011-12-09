
import java.util.*;
import gnu.io.*;
import java.io.*;

public class SimpleRead implements Runnable, SerialPortEventListener {
    static CommPortIdentifier portId;
    static Enumeration portList;

    static OutputStream os;
    InputStream inputStream;
    SerialPort serialPort;
    Thread readThread;

    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        portList = CommPortIdentifier.getPortIdentifiers();
        SimpleRead reader = null;
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.println("serial " + portId.getName() );
                // if (portId.getName().equals("COM9")) {
                if (portId.getName().equals("/dev/ttyUSB0")) {
                    SimpleRead reader1 = new SimpleRead();
                }
            }
        }
        if ( reader == null )
        {
        	System.out.println("nao consegui ligar");
        	System.exit(-1);
        }
        Scanner sc = new Scanner(System.in);
        while (true){
        	String c = sc.next();
        	os.write(c.getBytes("ASCII"));
        }
    }

    public SimpleRead() {
        try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
        } catch (PortInUseException e) {System.out.println(e);}
        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {System.out.println(e);}
	try {
            serialPort.addEventListener(this);
            os = serialPort.getOutputStream();
	} catch (TooManyListenersException e) {System.out.println(e);} 
	catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(38400,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {System.out.println(e);}
        readThread = new Thread(this);
        readThread.start();
    }

    public void run() {
        try {
        	os = serialPort.getOutputStream();
            Thread.sleep(200);
        } catch (InterruptedException e) {System.out.println(e);} 
        catch (IOException e) {
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
