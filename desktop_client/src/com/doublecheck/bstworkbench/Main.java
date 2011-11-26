package com.doublecheck.bstworkbench;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.doublecheck.bstworkbench.ui.TextComponentDemo;

public final class Main {

	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        final TextComponentDemo frame = new TextComponentDemo();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    //The standard main method.
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
    /*	try {
        			UIManager.setLookAndFeel(
        			    UIManager.getSystemLookAndFeelClassName());
        		} catch (ClassNotFoundException e) {
        			e.printStackTrace();
        		} catch (InstantiationException e) {
        			e.printStackTrace();
        		} catch (IllegalAccessException e) {
        			e.printStackTrace();
        		} catch (UnsupportedLookAndFeelException e) {
        			e.printStackTrace();
        		}*/
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        		
                //Turn off metal's use of bold fonts
	        createAndShowGUI();
            }
        });
    }

}
