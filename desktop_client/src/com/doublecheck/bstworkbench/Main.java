package com.doublecheck.bstworkbench;

import java.awt.Dimension;

import javax.swing.SwingUtilities;

import com.doublecheck.bstworkbench.ui.Editor;

public final class Main {

	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        final Editor frame = new Editor();

        //Display the window.
        frame.setSize(new Dimension(600,600));
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
