package com.doublecheck.bstworkbench.ui;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;


import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.RecordableTextAction;


import com.doublecheck.bstworkbench.compiler.Compiler;
import com.doublecheck.bstworkbench.compiler.commands.Command;
import com.doublecheck.bstworkbench.io.IOUtil;

@SuppressWarnings("serial")
public class Editor extends JFrame  implements  SyntaxConstants{

    private final static String TITLE = "Microcontroller Workbench";

   // JTextPane textPane;
    private RTextScrollPane scrollPane;
    private RSyntaxTextArea textArea;

    // AbstractDocument doc;

    private JTextArea changeLog;

    // undo helpers
    protected RecordableTextAction undoAction;
    protected RecordableTextAction redoAction;

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
        
        
        textArea = createTextArea();
        textArea.setSyntaxEditingStyle(SYNTAX_STYLE_BST);
        scrollPane = new RTextScrollPane(textArea, true);
        Gutter gutter = scrollPane.getGutter();
        gutter.setBookmarkingEnabled(true);
        URL url = getClass().getClassLoader().getResource("bookmark.png");
        gutter.setBookmarkIcon(new ImageIcon(url));

       

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



        // Create the text area for the status log and configure it.
        changeLog = new JTextArea(5, 30);
        changeLog.setEditable(false);
        JScrollPane scrollPaneForLog = new JScrollPane(changeLog);

        // Create a split pane for the change log and the text area.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                scrollPane, scrollPaneForLog);
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
        final JMenuBar mb = new JMenuBar();
        mb.add(createFileMenu());
        mb.add(createEditMenu());
        mb.add(createActionMenu());
        mb.add(createThemeMenu());
        setJMenuBar(mb);

        // Add some key bindings.
        addBindings();

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                changesNotSaved();
            }
        });
        

        // Start watching for undoable edits and caret changes.
        textArea.getDocument().addUndoableEditListener(
                new MyUndoableEditListener());
        textArea.addCaretListener(caretListenerLabel);
        
    }
    
    /**
     * Creates the text area for this application.
     *
     * @return The text area.
     */
    private RSyntaxTextArea createTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea(25, 50);
        textArea.setCaretPosition(0);
        textArea.requestFocusInWindow();
        textArea.setMarkOccurrences(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setCodeFoldingEnabled(true);
        return textArea;
    }


    protected JPanel addButtons() {
        final JPanel buttonsPanel = new JPanel();
        final GroupLayout layout = new GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        final JButton compile = new JButton(compileAction);
        
        final JButton upload = new JButton("Upload"); 
        final JButton run = new JButton("Run");
        run.setEnabled(false);
        final JButton step = new JButton("Step ");
        step.setEnabled(false);
        final JButton stop = new JButton("Stop");
        stop.setEnabled(false);

        compile.setAction(compileAction);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(compile)
                    .addComponent(upload)
                    .addComponent(run)
                    .addComponent(step)
                    .addComponent(stop));
        layout.setVerticalGroup(layout.createSequentialGroup()
                    .addComponent(compile)
                    .addComponent(upload)
                    .addGap(20).addGap(20)
                    .addComponent(run)
                    .addComponent(step)
                    .addComponent(stop));
        
        layout.linkSize(SwingConstants.HORIZONTAL, compile, upload);
        layout.linkSize(SwingConstants.HORIZONTAL, compile, run);
        layout.linkSize(SwingConstants.HORIZONTAL, compile, step);
        layout.linkSize(SwingConstants.HORIZONTAL, compile, stop);

        return buttonsPanel;
    }

    // This one listens for edits that can be undone.
    protected class MyUndoableEditListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
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
        InputMap inputMap = textArea.getInputMap();
        KeyStroke key = null;
        

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
        undoAction = RTextArea.getAction(RTextArea.UNDO_ACTION);
        menu.add(undoAction);

        redoAction = RTextArea.getAction(RTextArea.REDO_ACTION);
        menu.add(redoAction);

        menu.addSeparator();

        // These actions come from the default editor kit.
        // Get the ones we want and stick them in the menu.
        menu.add(RTextArea.getAction(RTextArea.CUT_ACTION));
        menu.add(RTextArea.getAction(RTextArea.COPY_ACTION));
        menu.add(RTextArea.getAction(RTextArea.PASTE_ACTION));

        menu.addSeparator();

        menu.add(RTextArea.getAction(RTextArea.SELECT_ALL_ACTION));
        return menu;
    }
    

    private JMenu createThemeMenu() {

        final JMenu menu = new JMenu("Themes");
        menu.add(new JMenuItem(new ThemeAction("Default", "/default.xml")));
        menu.add(new JMenuItem(new ThemeAction("Dark", "/dark.xml")));
        menu.add(new JMenuItem(new ThemeAction("Eclipse", "/eclipse.xml")));
        menu.add(new JMenuItem(new ThemeAction("Visual Studio", "/vs.xml")));

        return menu;

    }





    class CompileAction extends AbstractAction {
        public CompileAction() {
            super("Compile");
        }

        public void actionPerformed(ActionEvent e) {
            if ( textArea.getText().trim().length() == 0 )
            {
                changeLog.append("There is nothing to compile\n");
                return;
            }
            Compiler parser = new Compiler();
            if ( changeLog.getText().length() != 0 )//get some space
                changeLog.append("\n\n\n");
            changeLog.append("Compiling\n");
            parser.parse(((RSyntaxDocument)textArea.getDocument()));
            if ( parser.detectedErrors() )
            {
                for ( String s : parser.getListErrors() )
                    changeLog.append(s+'\n');
                changeLog.append("Found errors while compiling\n");
            }
            else
            {
                changeLog.append("Compiled successfully\n");
                for ( Command s : parser.getCommands() )
                    changeLog.append(s.toString()+'\n');
            }
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
                textArea.setText(IOUtil.readFile(currentFile));
                textArea.discardAllEdits();
               // redoAction.updateRedoState();
              //  undoAction.updateUndoState();
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
                if (!IOUtil.saveFile(currentFile, textArea.getText())) {
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
                if ( IOUtil.saveFile(currentFile, textArea.getText()))
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
                textArea.setText("");
                textArea.discardAllEdits();
                edited = false;
        }
    }
    
    
    // Theme support

    private class ThemeAction extends AbstractAction {

        private String xml;

        public ThemeAction(String name, String xml) {
            putValue(NAME, name);
            this.xml = xml;
        }

        public void actionPerformed(ActionEvent e) {
            InputStream in = getClass().getResourceAsStream(xml);
            try {
               Theme theme = Theme.load(in);
               theme.apply(textArea);
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
        }

    }
   
}
