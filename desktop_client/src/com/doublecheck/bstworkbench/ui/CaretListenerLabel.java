package com.doublecheck.bstworkbench.ui;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

// This listens for and reports caret movements.
@SuppressWarnings("serial")
public class CaretListenerLabel extends JLabel implements CaretListener {
	public CaretListenerLabel(String label) {
		super(label);
	}

	// Might not be invoked from the event dispatch thread.
	public void caretUpdate(CaretEvent e) {
		displaySelectionInfo(e.getDot(), (JTextComponent) e.getSource());
	}

    public int getRow(int pos, JTextComponent editor) {
        int rn =  0;
        try {
            int offs=pos;
            while( offs>0) {
                offs=Utilities.getRowStart(editor, offs)-1;
                rn++;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return rn;
    }

    public int getColumn(int pos, JTextComponent editor) {
        try {
            return pos-Utilities.getRowStart(editor, pos)+1;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return -1;
    }

	// This method can be invoked from any thread. It
	// invokes the setText and modelToView methods, which
	// must run on the event dispatch thread. We use
	// invokeLater to schedule the code for execution
	// on the event dispatch thread.
	protected void displaySelectionInfo(final int dot, final JTextComponent editor ) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    int row = 0;
                int col = 0;
			    if ( editor instanceof RSyntaxTextArea ){
			        row = ((RSyntaxTextArea)editor).getCaretLineNumber()+1;
			        col = ((RSyntaxTextArea)editor).getCaretOffsetFromLineStart();
			    }
			    else{
			        row = getRow(dot , editor );
	                col = getColumn(dot , editor );
			    }
				
				setText("Position  " + row + ":" + col);
			}
		});
	}
}