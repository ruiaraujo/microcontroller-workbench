package com.doublecheck.bstworkbench.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
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


import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;


import com.doublecheck.bstworkbench.Resources;
import com.doublecheck.bstworkbench.compiler.Compiler;
import com.doublecheck.bstworkbench.compiler.Instruction;
import com.doublecheck.bstworkbench.compiler.commands.Command;
import com.doublecheck.bstworkbench.io.IOUtil;
import com.doublecheck.bstworkbench.io.MicrocontrollerManager;
import com.doublecheck.bstworkbench.io.rs232.SerialManager;

@SuppressWarnings("serial")
public class Editor extends JFrame  implements  SyntaxConstants , 
												MicrocontrollerManager.AcknowledgementListener{

    private final static String TITLE = "Microcontroller Workbench";

    private RTextScrollPane scrollPane;
    private RSyntaxTextArea textArea;

    private JTextArea changeLog;

    private Resources resources = Resources.getInstance();
    
    //SVF hanlding
    private final CompileAction compileAction;
    private final UploadAction uploadAction;

    private final RunTestAction runTestAction;
    private final StopTestAction stopTestAction;
    private final StepTestAction stepTestAction;
    private final MicrocontrollerManager manager;
    private String portName;


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
        
        manager = new SerialManager();
        manager.addAcknowledgementListener(this);
        compileAction = new CompileAction();
        openFileAction = new OpenFileAction();
        runTestAction = new RunTestAction();
        stopTestAction = new StopTestAction();
        stepTestAction = new StepTestAction();
        saveFileAction = new SaveFileAction(false);
        saveFileAsAction = new SaveFileAction(true);
        newFileAction = new NewFileAction();
        uploadAction = new UploadAction();
        portName = resources.getPort(MicrocontrollerManager.SERIAL_PREF_KEY);
        textArea = createTextArea();
        textArea.setSyntaxEditingStyle(SYNTAX_STYLE_SVF);
        scrollPane = new RTextScrollPane(textArea, true);
        Gutter gutter = scrollPane.getGutter();
        gutter.setBookmarkingEnabled(true);
        URL url = getClass().getClassLoader().getResource("bookmark.png");
        gutter.setBookmarkIcon(new ImageIcon(url));
        
        // Install auto-completion onto our text area.
        AutoCompletion ac = new AutoCompletion(createCompletionProvider());
        ac.setListCellRenderer(new CCellRenderer());
        ac.setShowDescWindow(true);
		ac.setParameterAssistanceEnabled(true);
        ac.install(textArea);
        ac.setAutoCompleteEnabled(true);
        ac.setAutoActivationDelay(500);
        ac.setAutoActivationEnabled(true);
        ac.setAutoCompleteSingleChoices(true);

        chooser = new JFileChooser(System.getProperty("user.dir")) {
            @Override
            public void approveSelection() {
                File f = getSelectedFile();
                String path = f.getAbsolutePath();
                if ( !path.endsWith("svf") )
                    f = new File(path+".svf");
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
        chooser.addChoosableFileFilter(new EasyFileFilter(new String[]{"svf"}, "Serial Vector Format"));
            


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
     * Returns the provider to use when editing code.
     *
     * @return The provider.
     * @see #createCommentCompletionProvider()
     * @see #createStringCompletionProvider()
     */
    private CompletionProvider createCodeCompletionProvider() {

        // Add completions for the BST.
        DefaultCompletionProvider cp = new DefaultCompletionProvider();

		// First try loading resource (running from demo jar), then try
		// accessing file (debugging in Eclipse).
		ClassLoader cl = getClass().getClassLoader();
		InputStream in = cl.getResourceAsStream("svf.xml");
		try {
			if (in!=null) {
				cp.loadFromXML(in);
				in.close();
			}
			else {
				cp.loadFromXML(new File("svf.xml"));
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		cp.setAutoActivationRules(true,"");
        return cp;

    }


    /**
     * Returns the provider to use when in a comment.
     *
     * @return The provider.
     * @see #createCodeCompletionProvider()
     * @see #createStringCompletionProvider()
     */
    private CompletionProvider createCommentCompletionProvider() {
        DefaultCompletionProvider cp = new DefaultCompletionProvider();
        cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
        cp.addCompletion(new BasicCompletion(cp, "FIXME:", "A bug that needs to be fixed"));
        return cp;
    }


    /**
     * Creates the completion provider for a C editor.  This provider can be
     * shared among multiple editors.
     *
     * @return The provider.
     */
    private CompletionProvider createCompletionProvider() {

        // Create the provider used when typing code.
        CompletionProvider codeCP = createCodeCompletionProvider();

        // The provider used when typing a comment.
        CompletionProvider commentCP = createCommentCompletionProvider();

        // Create the "parent" completion provider.
        LanguageAwareCompletionProvider provider = new
                                LanguageAwareCompletionProvider(codeCP);
        provider.setCommentCompletionProvider(commentCP);

        return provider;

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
        
        final JButton upload = new JButton(uploadAction); 
        final JButton run = new JButton(runTestAction);
        //run.setEnabled(false);
        final JButton step = new JButton(stepTestAction);
        //step.setEnabled(false);
        final JButton stop = new JButton(stopTestAction);
        //stop.setEnabled(false);

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
        key = (KeyStroke) newFileAction.getValue(Action.ACCELERATOR_KEY);
        inputMap.put(key, newFileAction);
        
        // Ctrl-o to open an file
        key = (KeyStroke) openFileAction.getValue(Action.ACCELERATOR_KEY);
        inputMap.put(key, openFileAction);
        

        // Ctrl-s to save an file
        key = (KeyStroke) saveFileAction.getValue(Action.ACCELERATOR_KEY);
        inputMap.put(key, saveFileAction);
        

        // Ctrl-Shift-s to save an file
        key = (KeyStroke) saveFileAsAction.getValue(Action.ACCELERATOR_KEY);
        inputMap.put(key, saveFileAsAction); 

        // Alt-c to compile
        key = (KeyStroke) compileAction.getValue(Action.ACCELERATOR_KEY);
        inputMap.put(key, compileAction);
    }

    // Create the edit menu.
    protected JMenu createFileMenu() {
        final JMenu menu = new JMenu("File");
        menu.setMnemonic('F'); // set mnemonic to F
        
        menu.add(newFileAction);
        menu.add(openFileAction);

        menu.addSeparator();
        
        menu.add(saveFileAction);
        menu.add(saveFileAsAction);
        menu.addSeparator();

        final JMenuItem aboutItem = new JMenuItem("About");
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
        final JMenu menu = new JMenu("Action");
        menu.setMnemonic('A'); // set mnemonic to F
        menu.add(compileAction);
        menu.add(uploadAction);

        menu.addSeparator();
        
        menu.add(runTestAction);
        menu.add(stepTestAction);
        menu.add(stopTestAction);
        
        return menu;
    }

    // Create the edit menu.
    protected JMenu createEditMenu() {
        final JMenu menu = new JMenu("Edit");

        // Undo and redo are actions of our own creation.
        menu.add(RTextArea.getAction(RTextArea.UNDO_ACTION));

        menu.add(RTextArea.getAction(RTextArea.REDO_ACTION));

        menu.addSeparator();

        // These actions come from the default editor kit.
        // Get the ones we want and stick them in the menu.
        menu.add(RTextArea.getAction(RTextArea.CUT_ACTION));
        menu.add(RTextArea.getAction(RTextArea.COPY_ACTION));
        menu.add(RTextArea.getAction(RTextArea.PASTE_ACTION));

        menu.addSeparator();

        menu.add(RTextArea.getAction(RTextArea.SELECT_ALL_ACTION));
        menu.addSeparator();
        JMenuItem preferences = new JMenuItem("Preferences");
        preferences.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
               JDialog preferencesDialog =  new CommChooserDialog(Editor.this);
               preferencesDialog.setVisible(true);
               portName = resources.getPort(MicrocontrollerManager.SERIAL_PREF_KEY);
            }
        });
        menu.add(preferences);
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
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_C, ActionEvent.ALT_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if ( textArea.getText().trim().length() == 0 )
            {
                changeLog.append("There is nothing to compile\n");
                return;
            }
            Compiler compiler = new Compiler();
            if ( changeLog.getText().length() != 0 )//get some space
                changeLog.append("\n\n\n");
            changeLog.append("Compiling\n");
            compiler.parse(((RSyntaxDocument)textArea.getDocument()));
            if ( compiler.detectedErrors() )
            {
                for ( String s : compiler.getListErrors() )
                    changeLog.append(s+'\n');
                changeLog.append("Found errors while compiling\n");
            }
            else
            {
                changeLog.append("Compiled successfully\n");
                for ( Command s : compiler.getCommands() )
                    changeLog.append(s.toString()+'\n');
                FileOutputStream os = null;
                try {
					os = new FileOutputStream(new File("compiled.o"));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				compiledOutput = compiler.getResult().getOutputFile();
                for ( Instruction s : compiler.getResult().getInstructions() )
                {
                    changeLog.append(s.toString()+'\n');
                }
                for ( byte[] b : compiledOutput )
                {
                	try {
						os.write(b);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                }
                try {
                	os.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

            }
        }
    }
    
    private List<byte[]> compiledOutput;
    
    class UploadAction extends AbstractAction {
        public UploadAction() {
            super("Upload");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_U, ActionEvent.ALT_MASK));
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( portName == null ){
                changeLog.append("Use the preferences to set up the serial port.");
                return;
            }
            if ( !manager.isConnected() )
            {
                if ( !manager.connect(portName) )
                {
                    changeLog.append("Error using the serial port.\n");
                    changeLog.append("Make sure that no other port is trying to use the serial port.\n");
                    return ;
                }
            }
            if ( compiledOutput.size() == 0 )
            {
                changeLog.append("Please compile first.\n");
                return;
            }
        	manager.initProgram();
        	for ( byte[] b : compiledOutput )
        			manager.write(b);
        	manager.finishProgram();
        }
        
    }
    
    class RunTestAction extends AbstractAction {
        public RunTestAction() {
            super("Run");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_R, ActionEvent.ALT_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( portName == null ){
                changeLog.append("Use the preferences to set up the serial port.");
                return;
            }
            if ( !manager.isConnected() )
            {
                if ( !manager.connect(portName) )
                {
                    changeLog.append("Error using the serial port.\n");
                    changeLog.append("Make sure that no other port is trying to use the serial port.\n");
                    return ;
                }
            }
            manager.runProgram();
        }
    }
    
    class StopTestAction extends AbstractAction {
        public StopTestAction() {
            super("Stop");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_S, ActionEvent.ALT_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( portName == null ){
                changeLog.append("Use the preferences to set up the serial port.");
                return;
            }
            if ( !manager.isConnected() )
            {
                if ( !manager.connect(portName) )
                {
                    changeLog.append("Error using the serial port.\n");
                    changeLog.append("Make sure that no other port is trying to use the serial port.\n");
                    return ;
                }
            }
            manager.stopProgram();
          //  ((SerialManager)manager).debugProgram();
        }
    }
    
    class StepTestAction extends AbstractAction {
        public StepTestAction() {
            super("Step");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_T, ActionEvent.ALT_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ( portName == null ){
                changeLog.append("Use the preferences to set up the serial port.");
                return;
            }
            if ( !manager.isConnected() )
            {
                if ( !manager.connect(portName) )
                {
                    changeLog.append("Error using the serial port.\n");
                    changeLog.append("Make sure that no other port is trying to use the serial port.\n");
                    return ;
                }
            }
            manager.stepProgram();
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
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if (chooser.showOpenDialog(Editor.this) == JFileChooser.APPROVE_OPTION) {
                currentFile = chooser.getSelectedFile();
                textArea.setText(IOUtil.readFile(currentFile));
                textArea.discardAllEdits();
                edited = false;
                setTitle(TITLE + " - " + currentFile.getAbsolutePath());
            }
        }
    }

    // the 'edited' must be set to false after successfully saving the file
    class SaveFileAction extends AbstractAction {
        private final boolean alwaysChooseFile;
        public SaveFileAction(boolean alwaysChooseFile) {
            super("Save File");
            if ( alwaysChooseFile )
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                        KeyEvent.VK_S, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
            else
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
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
                String path = currentFile.getAbsolutePath();
                if ( !path.endsWith("svf") )
                    currentFile = new File(path+".svf");
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
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_N, ActionEvent.CTRL_MASK));
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

	public void onAckReceived() {
		// TODO Auto-generated method stub
		
	}


	public void onErrorAckReceived() {
		// TODO Auto-generated method stub
		
	}


	public void onTDISent(String received) {
		// TODO Auto-generated method stub
		
	}


	public void onTDOReceived(String received) {
		// TODO Auto-generated method stub
		
	}
   
}
