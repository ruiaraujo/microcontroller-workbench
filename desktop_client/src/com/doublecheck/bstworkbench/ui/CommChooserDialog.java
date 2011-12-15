package com.doublecheck.bstworkbench.ui;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.doublecheck.bstworkbench.Resources;
import com.doublecheck.bstworkbench.io.MicrocontrollerManager;
import com.doublecheck.bstworkbench.io.rs232.Rs232Utils;

@SuppressWarnings("serial")
public class CommChooserDialog extends JDialog implements ActionListener {
    private final static String NO_SERIAL_DETECTED = "No serial ports.";
    private final Resources resources = Resources.getInstance();
    private final JComboBox petList;
    @SuppressWarnings("unchecked")
    public CommChooserDialog(JFrame parent) {
        super(parent);
        setTitle("Port Selection");
        setLocationRelativeTo(parent);
        setModalityType(ModalityType.APPLICATION_MODAL);
        final JPanel base = new JPanel();
        final JLabel label = new JLabel("Select Port:");
        base.add(label);
        petList = new JComboBox(Rs232Utils.getPortList().toArray(new String[0]));
        final ComboBoxModel model = petList.getModel();
        if ( model.getSize() == 0 )
            petList.setModel(new DefaultComboBoxModel(new String[]{NO_SERIAL_DETECTED}));
        else
        {
            petList.setSelectedIndex(0);
            final String previousChoice = resources.getPort(MicrocontrollerManager.SERIAL_PREF_KEY);
            if ( previousChoice == null )
                resources.setPort(MicrocontrollerManager.SERIAL_PREF_KEY,(String) petList.getSelectedItem() );
            else
            {
                for ( int i = 0; i < model.getSize() ; ++i)
                {
                    if ( model.getElementAt(i).equals(previousChoice) )
                    {
                        petList.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
        }
        petList.addActionListener(this);
        base.add(petList);
        setContentPane(base);
        pack();
        final Dimension size = getPreferredSize();
        size.height += 15;
        size.width += 15;
        setMinimumSize(size);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( petList.getModel().getSize() == 1 && 
                petList.getSelectedItem().equals(NO_SERIAL_DETECTED))
            return;
            
        resources.setPort(MicrocontrollerManager.SERIAL_PREF_KEY,(String) petList.getSelectedItem() );
    }
}
