package com.doublecheck.bstworkbench.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import com.doublecheck.bstworkbench.compiler.parser.Parser;
import com.doublecheck.bstworkbench.io.IOUtil;

@SuppressWarnings("serial")
public class Editor extends JFrame {

    private final static String TITLE = "Microcontroller Workbench";

    JTextPane textPane;
    // AbstractDocument doc;

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

    // Simple file management choices
    private final JFileChooser chooser;
    private File currentFile;
    private final Action openFileAction;
    private final Action saveFileAction;
    private final Action saveFileAsAction;
    private final Action newFileAction;

    private boolean edited = false;
    public Editor() {
        super(TITLE+ " - New File");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        compileAction = new CompileAction();
        openFileAction = new OpenFileAction();
        saveFileAction = new SaveFileAction(false);
        saveFileAsAction = new SaveFileAction(true);
        newFileAction = new NewFileAction();
        // Create the text pane and configure it.
        textPane = new JTextPane() // line numbers stay in sync
        {
            public void paint(Graphics g) {
                super.paint(g);
                lines.repaint();
            }

            // No wrap
            public boolean getScrollableTracksViewportWidth() {
                return getUI().getPreferredSize(this).width <= getParent()
                        .getSize().width;
            }
        };

        chooser = new JFileChooser(System.getProperty("user.dir")) {
            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,
                            "The file exists, overwrite?", "Existing file",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                    case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                    case JOptionPane.NO_OPTION:
                        return;
                    case JOptionPane.CANCEL_OPTION:

                        return;
                    }
                }
                super.approveSelection();
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

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                changesNotSaved();
            }
        });
        
        // Put the initial text into the text pane.
        textPane.setCaretPosition(0);

        // Start watching for undoable edits and caret changes.
        textPane.getStyledDocument().addUndoableEditListener(
                new MyUndoableEditListener());
        textPane.addCaretListener(caretListenerLabel);
        
    }

    protected JPanel addButtons() {
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
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(compile)
                .addComponent(run).addComponent(step).addComponent(stop));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(
                compile).addGap(20).addComponent(run).addComponent(step)
                .addComponent(stop));

        layout.linkSize(SwingConstants.HORIZONTAL, compile, run);
        layout.linkSize(SwingConstants.HORIZONTAL, compile, step);
        layout.linkSize(SwingConstants.HORIZONTAL, compile, stop);

        return buttonsPanel;
    }

    // This one listens for edits that can be undone.
    protected class MyUndoableEditListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            // Remember the edit and update the menus.
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
            edited = true;
            if ( currentFile == null )
                setTitle(TITLE + " - *New File");
            else
                setTitle(TITLE + " - *" + currentFile.getAbsolutePath());
        }
    }

    // Add a couple of emacs key bindings for navigation.
    // Add also undo and redo bindings
    protected void addBindings() {
        InputMap inputMap = textPane.getInputMap();
        KeyStroke key = null;
        
        // Ctrl-z to undo
        key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
        inputMap.put(key, undoAction);

        // Ctrl-y to redo
        key = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);
        inputMap.put(key, redoAction);

        // Ctrl-n to open an file
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        inputMap.put(key, newFileAction);
        
        // Ctrl-o to open an file
        key = KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK);
        inputMap.put(key, openFileAction);
        

        // Ctrl-s to save an file
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
        inputMap.put(key, saveFileAction);
        

        // Ctrl-Shift-s to save an file
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK|Event.SHIFT_MASK);
        inputMap.put(key, saveFileAsAction); 

        // Alt-c to compile
        key = KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.ALT_MASK);
        inputMap.put(key, compileAction);

    }

    // Create the edit menu.
    protected JMenu createFileMenu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic('F'); // set mnemonic to F
        
        JMenuItem newFile = new JMenuItem("New File");
        newFile.addActionListener(newFileAction);
        menu.add(newFile);
        
        JMenuItem openFile = new JMenuItem("Open File");
        openFile.addActionListener(openFileAction);
        menu.add(openFile);

        menu.addSeparator();
        
        JMenuItem saveFile = new JMenuItem("Save File");
        saveFile.addActionListener(saveFileAction);
        menu.add(saveFile);
        
        JMenuItem saveAsFile = new JMenuItem("Save File As");
        menu.add(saveAsFile);
        menu.addSeparator();

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        menu.add(aboutItem);

        aboutItem.addActionListener(new ActionListener() {
            // display message dialog when user selects About...
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(Editor.this,
                        "Microcontroller Workbench\n\nYes we can!", "About",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        return menu;
    }

    // Create the edit menu.
    protected JMenu createActionMenu() {
        JMenu menu = new JMenu("Action");
        menu.setMnemonic('A'); // set mnemonic to F
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
            if ( textPane.getText().trim().length() == 0 )
            {
                changeLog.append("There is nothing to compile\n");
                return;
            }
            Parser parser = new Parser();
            if ( changeLog.getText().length() != 0 )//get some space
                changeLog.append("\n\n\n");
            changeLog.append("Compiling\n");
            parser.parse(textPane.getText());
            if ( parser.detectedErrors() )
            {
                for ( String s : parser.getListErrors() )
                    changeLog.append(s+'\n');
                changeLog.append("Found erros while compiling\n");
            }
            else
                changeLog.append("Compiled successfully\n");
        }

    }
    
    
    //File Management Actions and functions
    
    protected void changesNotSaved(){
        if ( edited )
        {
            int result = JOptionPane.showConfirmDialog(this,
                    "There are unsaved changes.\nDo you want to save them?", "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            switch (result) {
            case JOptionPane.YES_OPTION:
                saveFileAction.actionPerformed(null);
                if ( !edited ) // saveFileAction sets edited to false when saves a file
                    System.exit(0);
                return;
            case JOptionPane.NO_OPTION:
                System.exit(0);
                return;
            case JOptionPane.CANCEL_OPTION:
                return;
            }
        }
        System.exit(0);
    }
    

    class OpenFileAction extends AbstractAction {
        public OpenFileAction() {
            super("Open File");
        }

        public void actionPerformed(ActionEvent e) {
            if (chooser.showOpenDialog(Editor.this) == JFileChooser.APPROVE_OPTION) {
                currentFile = chooser.getSelectedFile();
                setTitle(TITLE + " - " + currentFile.getAbsolutePath());
                textPane.setText(IOUtil.readFile(currentFile));
                undo.discardAllEdits();
                redoAction.updateRedoState();
                undoAction.updateUndoState();
                edited = false;
            }
        }
    }

    // the 'edited' must be set to false after successfully saving the file
    class SaveFileAction extends AbstractAction {
        private final boolean alwaysChooseFile;
        public SaveFileAction(boolean alwaysChooseFile) {
            super("Save File");
            this.alwaysChooseFile = alwaysChooseFile;
        }

        public void actionPerformed(ActionEvent e) {
            if (currentFile != null && !alwaysChooseFile) {
                if (!IOUtil.saveFile(currentFile, textPane.getText())) {
                    JOptionPane.showMessageDialog(Editor.this,
                            "There was an error while saving:\n"
                                    + currentFile.getAbsolutePath(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    setTitle(TITLE + " - " + currentFile.getAbsolutePath());
                    edited = false;
                }
                return;
            }
            if (chooser.showSaveDialog(Editor.this) == JFileChooser.APPROVE_OPTION) {
                currentFile = chooser.getSelectedFile();
                if ( IOUtil.saveFile(currentFile, textPane.getText()))
                {
                    edited = false;
                    setTitle(TITLE + " - " + currentFile.getAbsolutePath());
                }
            }
        }
    }
    

    class NewFileAction extends AbstractAction {
        public NewFileAction() {
            super("New File");
        }

        public void actionPerformed(ActionEvent e) {
                currentFile = null;
                setTitle(TITLE+ " - New File");
                textPane.setText("");
                undo.discardAllEdits();
                redoAction.updateRedoState();
                undoAction.updateUndoState();
                edited = false;
        }
    }
}
