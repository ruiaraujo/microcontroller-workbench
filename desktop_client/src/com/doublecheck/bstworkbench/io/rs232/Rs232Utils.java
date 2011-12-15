package com.doublecheck.bstworkbench.io.rs232;

import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class Rs232Utils {
    private Rs232Utils(){}
    
    public static List<String> getPortList(){
        final List<String> portsAvailable = new ArrayList<String>();
        final Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier portId = null;
        while (portList.hasMoreElements()) {
            portId = portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.println("serial " + portId.getName());
                portsAvailable.add(portId.getName());
            }
        }
        return portsAvailable;
    }
    
    public static boolean hasAvailablePorts(){
        final Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier portId = null;
        while (portList.hasMoreElements()) {
            portId = portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                return true;
            }
        }
        return false;
    }
}
