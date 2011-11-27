package com.doublecheck.bstworkbench.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;


@SuppressWarnings("serial")
public class Editor extends JFrame {
	JTextPane textPane;
	//AbstractDocument doc;

	JTextArea changeLog;
	String newline = "\n";
	HashMap<Object, Action> actions;

	// lines number
	private LineNumbers lines;

	// undo helpers
	protected UndoAction undoAction;
	protected RedoAction redoAction;
	protected UndoManager undo = new UndoManager();

	protected CompileAction compileAction;
	
	
	
	
	public Editor() {
		super("Microcontroller Workbench");
		compileAction = new CompileAction();
		// Create the text pane and configure it.
		textPane = new JTextPane() // line numbers stay in sync
		{
			public void paint(Graphics g) {
				super.paint(g);
				lines.repaint();
			}
			//No wrap
			public boolean getScrollableTracksViewportWidth() {
				return getUI().getPreferredSize(this).width <= getParent()
						.getSize().width;
			}
		};

		textPane.setCaretPosition(0);
		textPane.setMargin(new Insets(5, 5, 5, 5));
		
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(new Dimension(200, 200));

		lines = new LineNumbers(scrollPane, textPane);
		JPanel editor = new JPanel(new BorderLayout());
		editor.add(lines, BorderLayout.LINE_START);
		editor.add(scrollPane, BorderLayout.CENTER);

		// Create the text area for the status log and configure it.
		changeLog = new JTextArea(5, 30);
		changeLog.setEditable(false);
		JScrollPane scrollPaneForLog = new JScrollPane(changeLog);

		// Create a split pane for the change log and the text area.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				editor, scrollPaneForLog);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(1.0);
		// Create the status area.
		JPanel statusPane = new JPanel(new GridLayout(1, 1));
		CaretListenerLabel caretListenerLabel = new CaretListenerLabel(" ");
		statusPane.add(caretListenerLabel);

		// Add the components.
		getContentPane().add(splitPane, BorderLayout.CENTER);
		getContentPane().add(statusPane, BorderLayout.PAGE_END);
		getContentPane().add(addButtons(), BorderLayout.LINE_END);

		// Set up the menu bar.
		actions = createActionTable(textPane);
		JMenu fileMenu = createFileMenu();
		JMenu editMenu = createEditMenu();
		JMenu actionMenu = createActionMenu();
		JMenuBar mb = new JMenuBar();
		mb.add(fileMenu);
		mb.add(editMenu);
		mb.add(actionMenu);
		setJMenuBar(mb);

		// Add some key bindings.
		addBindings();

		// Put the initial text into the text pane.
		textPane.setCaretPosition(0);

		// Start watching for undoable edits and caret changes.
		textPane.getStyledDocument().addUndoableEditListener(new MyUndoableEditListener());
		textPane.addCaretListener(caretListenerLabel);
	}
	
	private JPanel getPanelAround(final Component c)
	{
		final JPanel panel = new JPanel();
		panel.add(c);
		return panel;
		
	}

	protected JPanel addButtons(){
		final JPanel buttonsPanel = new JPanel();
		final GroupLayout layout = new GroupLayout(buttonsPanel);
		buttonsPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		final JButton compile = new JButton(compileAction);
		final JButton run = new JButton("Run");
		final JButton step = new JButton("Step");
		final JButton stop = new JButton("Stop");
		
		compile.setAction(compileAction);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(compile)
				.addComponent(run)
				.addComponent(step)
				.addComponent(stop)
			);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(compile)
				.addGap(20)
				.addComponent(run)
				.addComponent(step)
				.addComponent(stop)
		);
		
		layout.linkSize(SwingConstants.HORIZONTAL,compile , run);
		layout.linkSize(SwingConstants.HORIZONTAL,compile , step);
		layout.linkSize(SwingConstants.HORIZONTAL,compile , stop);

		
		return buttonsPanel;
	}

	// This one listens for edits that can be undone.
	protected class MyUndoableEditListener implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			// Remember the edit and update the menus.
			undo.addEdit(e.getEdit());
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	}

	// Add a couple of emacs key bindings for navigation.
	// Add also undo and redo bindings
	protected void addBindings() {
		InputMap inputMap = textPane.getInputMap();

		// Ctrl-b to go backward one character
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
		inputMap.put(key, DefaultEditorKit.backwardAction);

		// Ctrl-f to go forward one character
		key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
		inputMap.put(key, DefaultEditorKit.forwardAction);

		// Ctrl-p to go up one line
		key = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);
		inputMap.put(key, DefaultEditorKit.upAction);

		// Ctrl-n to go down one line
		key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
		inputMap.put(key, DefaultEditorKit.downAction);
		
		// Ctrl-z to undo
		key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
		inputMap.put(key, undoAction);

		// Ctrl-y to redo
		key = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
		inputMap.put(key, redoAction);

	}

	// Create the edit menu.
	protected JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.setMnemonic( 'F' ); // set mnemonic to F
		 
		JMenuItem openFile = new JMenuItem("Open File");
		menu.add( openFile );
		JMenuItem saveFile = new JMenuItem("Save File");
		menu.add( saveFile );
		JMenuItem saveAsFile = new JMenuItem("Save File As");
		menu.add( saveAsFile );
		menu.addSeparator();

		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.setMnemonic( 'A' );
		menu.add( aboutItem );
		
		aboutItem.addActionListener( new ActionListener()
			{
				// display message dialog when user selects About...
				public void actionPerformed( ActionEvent event )
				{
					JOptionPane.showMessageDialog( Editor.this, "Microcontroller Workbench\n\nYes we can!",
					"About", JOptionPane.PLAIN_MESSAGE );
				}
			}
		);
		return menu;
	}
	
	// Create the edit menu.
	protected JMenu createActionMenu() {
		JMenu menu = new JMenu("Action");
		menu.setMnemonic( 'A' ); // set mnemonic to F
		JMenuItem aboutItem = new JMenuItem(compileAction);
		menu.add(aboutItem);
		menu.addSeparator();
		
		return menu;
	}
	
	// Create the edit menu.
	protected JMenu createEditMenu() {
		JMenu menu = new JMenu("Edit");

		// Undo and redo are actions of our own creation.
		undoAction = new UndoAction();
		menu.add(undoAction);

		redoAction = new RedoAction();
		menu.add(redoAction);

		menu.addSeparator();

		// These actions come from the default editor kit.
		// Get the ones we want and stick them in the menu.
		menu.add(getActionByName(DefaultEditorKit.cutAction));
		menu.add(getActionByName(DefaultEditorKit.copyAction));
		menu.add(getActionByName(DefaultEditorKit.pasteAction));

		menu.addSeparator();

		menu.add(getActionByName(DefaultEditorKit.selectAllAction));
		return menu;
	}

	// The following two methods allow us to find an
	// action provided by the editor kit by its name.
	private HashMap<Object, Action> createActionTable(
			JTextComponent textComponent) {
		HashMap<Object, Action> actions = new HashMap<Object, Action>();
		Action[] actionsArray = textComponent.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			Action a = actionsArray[i];
			actions.put(a.getValue(Action.NAME), a);
		}
		return actions;
	}

	private Action getActionByName(String name) {
		return actions.get(name);
	}

	class UndoAction extends AbstractAction {
		public UndoAction() {
			super("Undo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undo.undo();
			} catch (CannotUndoException ex) {
				System.out.println("Unable to undo: " + ex);
				ex.printStackTrace();
			}
			updateUndoState();
			redoAction.updateRedoState();
		}

		protected void updateUndoState() {
			if (undo.canUndo()) {
				setEnabled(true);
				putValue(Action.NAME, undo.getUndoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Undo");
			}
		}
	}

	class RedoAction extends AbstractAction {
		public RedoAction() {
			super("Redo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undo.redo();
			} catch (CannotRedoException ex) {
				System.out.println("Unable to redo: " + ex);
				ex.printStackTrace();
			}
			updateRedoState();
			undoAction.updateUndoState();
		}

		protected void updateRedoState() {
			if (undo.canRedo()) {
				setEnabled(true);
				putValue(Action.NAME, undo.getRedoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Redo");
			}
		}
	}
	class CompileAction extends AbstractAction {
		public CompileAction() {
			super("Compile");
		}

		public void actionPerformed(ActionEvent e) {
			changeLog.append("Compiling\n");
		}

	}
}
