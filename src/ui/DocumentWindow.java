package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

public class DocumentWindow extends JFrame implements ActionListener, 
													  ComponentListener, 
													  FocusListener, 
													  HierarchyBoundsListener, 
													  KeyListener, 
													  MouseListener, 
													  WindowListener {
	private DocumentWindowDataSource dataSource;
	private DocumentWindowDelegate delegate;
	
	private int selectedView;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newMenuItem;
	private JMenuItem openMenuItem;
	private JMenuItem closeMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenu editMenu;
	private JMenuItem undoMenuItem;
	private JMenuItem redoMenuItem;
	private JMenuItem cutMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem findMenuItem;
	private JMenuItem replaceMenuItem;
	private JMenu viewMenu;
	private JCheckBoxMenuItem generalViewMenuItem;
	private JCheckBoxMenuItem translationsViewMenuItem;
	private JMenu windowMenu;
	private JMenuItem minimizeMenuItem;
	private JMenuItem zoomMenuItem;
	private JMenu helpMenu;
	private JMenuItem helpMenuItem;
	
	private JButton generalButton;
	private JButton translationsButton;
	
	private JPanel generalPanel;
	private JLabel projectFolderLabel;
	private JTextField projectFolderField;
	private JLabel languageFileLabel;
	private JTextField languageFileField;
	private JLabel languagesLabel;
	private ListView languagesListView;
	private JButton addLanguageButton;
	private JButton removeLanguageButton;
	
	private JPanel translationsPanel;
	
	private ListView languageSelectionListView;
	private TableView entriesTableView;
	private JButton addEntryButton;
	private JButton removeEntryButton;
	
	public DocumentWindow() {
		dataSource = null;
		delegate = null;
		
		selectedView = 0;
		
		setIconImage(Icons.appIcon().getImage());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(null);
		setMinimumSize(new Dimension(640, 360));
		setLocationRelativeTo(null);
		
		getContentPane().setBackground(Color.white);
		
		int modifiers = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		
		newMenuItem = new JMenuItem("New");
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke('N', modifiers));
		newMenuItem.addActionListener(this);
		
		openMenuItem = new JMenuItem("Open...");
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', modifiers));
		openMenuItem.addActionListener(this);
		
		closeMenuItem = new JMenuItem("Close");
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke('W', modifiers));
		closeMenuItem.addActionListener(this);
		
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', modifiers));
		saveMenuItem.addActionListener(this);
		
		saveAsMenuItem = new JMenuItem("Save As...");
		saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', modifiers | InputEvent.SHIFT_DOWN_MASK));
		saveAsMenuItem.addActionListener(this);
		
		fileMenu.add(newMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(closeMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		
		editMenu = new JMenu("Edit");
		
		undoMenuItem = new JMenuItem("Undo");
		undoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Z', modifiers));
		undoMenuItem.addActionListener(this);
		
		redoMenuItem = new JMenuItem("Redo");
		redoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Y', modifiers));
		redoMenuItem.addActionListener(this);
		
		cutMenuItem = new JMenuItem(new DefaultEditorKit.CutAction());
		cutMenuItem.setText("Cut");
		cutMenuItem.setAccelerator(KeyStroke.getKeyStroke('X', modifiers));
		
		copyMenuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
		copyMenuItem.setText("Copy");
		copyMenuItem.setAccelerator(KeyStroke.getKeyStroke('C', modifiers));
		
		pasteMenuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
		pasteMenuItem.setText("Paste");
		pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke('V', modifiers));
		
		findMenuItem = new JMenuItem("Find...");
		findMenuItem.setAccelerator(KeyStroke.getKeyStroke('F', modifiers));
		findMenuItem.addActionListener(this);
		
		replaceMenuItem = new JMenuItem("Find and Replace...");
		replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke('F', modifiers | InputEvent.ALT_DOWN_MASK));
		replaceMenuItem.addActionListener(this);
		
		editMenu.add(undoMenuItem);
		editMenu.add(redoMenuItem);
		editMenu.addSeparator();
		editMenu.add(cutMenuItem);
		editMenu.add(copyMenuItem);
		editMenu.add(pasteMenuItem);
		editMenu.addSeparator();
		editMenu.add(findMenuItem);
		editMenu.add(replaceMenuItem);
		
		viewMenu = new JMenu("View");
		
		generalViewMenuItem = new JCheckBoxMenuItem("General");
		generalViewMenuItem.setAccelerator(KeyStroke.getKeyStroke('1', modifiers));
		generalViewMenuItem.setState(true);
		generalViewMenuItem.addActionListener(this);
		
		translationsViewMenuItem = new JCheckBoxMenuItem("Translations");
		translationsViewMenuItem.setAccelerator(KeyStroke.getKeyStroke('2', modifiers));
		translationsViewMenuItem.setState(false);
		translationsViewMenuItem.addActionListener(this);
		
		viewMenu.add(generalViewMenuItem);
		viewMenu.add(translationsViewMenuItem);
		
		windowMenu = new JMenu("Window");
		
		minimizeMenuItem = new JMenuItem("Minimize");
		minimizeMenuItem.addActionListener(this);
		
		zoomMenuItem = new JMenuItem("Zoom");
		zoomMenuItem.setAccelerator(KeyStroke.getKeyStroke('M', modifiers | InputEvent.SHIFT_DOWN_MASK));
		zoomMenuItem.addActionListener(this);
		
		windowMenu.add(minimizeMenuItem);
		windowMenu.add(zoomMenuItem);
		
		helpMenu = new JMenu("Help");
		helpMenuItem = new JMenuItem("NanoTranslation Help");
		helpMenuItem.setAccelerator(KeyStroke.getKeyStroke('/', modifiers | InputEvent.SHIFT_DOWN_MASK));
		helpMenuItem.addActionListener(this);
		
		helpMenu.add(helpMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
		
		setJMenuBar(menuBar);
		
		generalButton = new JButton("General");
		generalButton.setOpaque(true);
		generalButton.setBackground(Color.lightGray);
		generalButton.setForeground(Color.black);
		generalButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.lightGray));
		generalButton.addActionListener(this);
		
		translationsButton = new JButton("Translations");
		translationsButton.setOpaque(true);
		translationsButton.setBackground(Color.white);
		translationsButton.setForeground(Color.black);
		translationsButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.lightGray));
		translationsButton.addActionListener(this);
		
		generalPanel = new JPanel();
		generalPanel.setBackground(Color.white);
		generalPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
		generalPanel.setLayout(null);
		
		projectFolderLabel = new JLabel("Project Folder:");
		projectFolderLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		projectFolderField = new JTextField();
		projectFolderField.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
		projectFolderField.addFocusListener(this);
		projectFolderField.addKeyListener(this);
		
		languageFileLabel = new JLabel("Language File:");
		languageFileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		languageFileField = new JTextField();
		languageFileField.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
		languageFileField.addFocusListener(this);
		languageFileField.addKeyListener(this);
		
		languagesLabel = new JLabel("Languages:");
		languagesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		languagesListView = new ListView();
		
		addLanguageButton = new JButton("+");
		addLanguageButton.setOpaque(true);
		addLanguageButton.setBackground(Color.white);
		addLanguageButton.setForeground(Color.black);
		addLanguageButton.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
		addLanguageButton.addActionListener(this);
		
		removeLanguageButton = new JButton("-");
		removeLanguageButton.setOpaque(true);
		removeLanguageButton.setBackground(Color.white);
		removeLanguageButton.setForeground(Color.black);
		removeLanguageButton.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
		removeLanguageButton.setEnabled(false);
		removeLanguageButton.addActionListener(this);
		
		generalPanel.add(projectFolderLabel);
		generalPanel.add(projectFolderField);
		generalPanel.add(languageFileLabel);
		generalPanel.add(languageFileField);
		generalPanel.add(languagesLabel);
		generalPanel.add(languagesListView);
		generalPanel.add(addLanguageButton);
		generalPanel.add(removeLanguageButton);
		
		translationsPanel = new JPanel();
		translationsPanel.setBackground(Color.white);
		translationsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
		translationsPanel.setVisible(false);
		translationsPanel.setLayout(null);
		
		languageSelectionListView = new ListView();
		
		entriesTableView = new TableView();
		
		addEntryButton = new JButton("+");
		addEntryButton.setOpaque(true);
		addEntryButton.setBackground(Color.white);
		addEntryButton.setForeground(Color.black);
		addEntryButton.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
		addEntryButton.addActionListener(this);
		
		removeEntryButton = new JButton("-");
		removeEntryButton.setOpaque(true);
		removeEntryButton.setBackground(Color.white);
		removeEntryButton.setForeground(Color.black);
		removeEntryButton.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
		removeEntryButton.setEnabled(false);
		removeEntryButton.addActionListener(this);
		
		translationsPanel.add(languageSelectionListView);
		translationsPanel.add(entriesTableView);
		translationsPanel.add(addEntryButton);
		translationsPanel.add(removeEntryButton);
		
		add(generalButton);
		add(translationsButton);
		add(generalPanel);
		add(translationsPanel);
		
		addComponentListener(this);
		addHierarchyBoundsListener(this);
		addWindowListener(this);
	}
	
	//DataSource & Delegate:
	public DocumentWindowDataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(DocumentWindowDataSource dataSource) {
		this.dataSource = dataSource;
		
		reloadData();
	}
	
	public DocumentWindowDelegate getDelegate() {
		return delegate;
	}
	
	public void setDelegate(DocumentWindowDelegate delegate) {
		this.delegate = delegate;
		
		reloadData();
	}
	
	//Public Methods:
	public int getSelectedView() {
		return selectedView;
	}
	
	public void setSelectedView(int selectedView) {
		if (selectedView < 0 || selectedView > 1) {
			return;
		}
		
		this.selectedView = selectedView;
		
		if (selectedView == 0) {
			generalViewMenuItem.setState(true);
			translationsViewMenuItem.setState(false);
			
			generalButton.setBackground(Color.lightGray);
			translationsButton.setBackground(Color.white);
			
			generalPanel.setVisible(true);
			translationsPanel.setVisible(false);
			
		}else if (selectedView == 1){
			generalViewMenuItem.setState(false);
			translationsViewMenuItem.setState(true);
			
			generalButton.setBackground(Color.white);
			translationsButton.setBackground(Color.lightGray);
			
			generalPanel.setVisible(false);
			translationsPanel.setVisible(true);
		}
	}
	
	public void reloadData() {
		String title = (getDataSource() != null) ? getDataSource().getTitle(this) : "";
		String projectFolder = (getDataSource() != null) ? getDataSource().getProjectFolder(this) : "";
		String languageFile = (getDataSource() != null) ? getDataSource().getLanguageFile(this) : "";
		boolean canRemoveLanguage = (getDelegate() != null) ? getDelegate().canRemoveLanguage(this) : false;
		
		setTitle(title);
		
		projectFolderField.setText(projectFolder);
		languageFileField.setText(languageFile);
		
		removeLanguageButton.setEnabled(canRemoveLanguage);
	}
	
	public ListView getLanguagesListView() {
		return languagesListView;
	}
	
	public ListView getLanguageSelectionListView() {
		return languageSelectionListView;
	}
	
	public TableView getEntriesTableView() {
		return entriesTableView;
	}
	
	//Positioning:
	private void updatePositioning() {
		int w = getContentPane().getWidth();
		int h = getContentPane().getHeight();
		
		generalButton.setLocation(0, 0);
		generalButton.setSize(100, 30);
		
		translationsButton.setLocation(100, 0);
		translationsButton.setSize(100, 30);
		
		generalPanel.setLocation(0, 29);
		generalPanel.setSize(w, h - 29);
		
		projectFolderLabel.setLocation(12, 12);
		projectFolderLabel.setSize(100, 25);
		
		projectFolderField.setLocation(118, 12);
		projectFolderField.setSize(300, 25);
		
		languageFileLabel.setLocation(12, 43);
		languageFileLabel.setSize(100, 25);
		
		languageFileField.setLocation(118, 43);
		languageFileField.setSize(300, 25);
		
		languagesLabel.setLocation(12, 74);
		languagesLabel.setSize(100, 25);
		
		languagesListView.setLocation(118, 74);
		languagesListView.setSize(300, Math.min(150, h - 146));
		
		addLanguageButton.setLocation(359, Math.min(230, h - 68));
		addLanguageButton.setSize(30, 25);
		
		removeLanguageButton.setLocation(388, Math.min(230, h - 68));
		removeLanguageButton.setSize(30, 25);
		
		translationsPanel.setLocation(0, 29);
		translationsPanel.setSize(w, h - 29);
		
		languageSelectionListView.setLocation(12, 12);
		languageSelectionListView.setSize(150, h - 53);
		
		entriesTableView.setLocation(172, 12);
		entriesTableView.setSize(w - 184, h - 84);
		
		addEntryButton.setLocation(w - 71, h - 66);
		addEntryButton.setSize(30, 25);
		
		removeEntryButton.setLocation(w - 42, h - 66);
		removeEntryButton.setSize(30, 25);
		
		revalidate();
		repaint();
	}
	
	//ActionListener:
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == newMenuItem) {
			if (getDelegate() != null) {
				getDelegate().pushedNew(this);
			}
			
		}else if (e.getSource() == openMenuItem) {
			if (getDelegate() != null) {
				getDelegate().pushedOpen(this);
			}
			
		}else if (e.getSource() == closeMenuItem) {
			if (getDelegate() != null) {
				getDelegate().pushedClose(this);
			}
			
		}else if (e.getSource() == saveMenuItem) {
			if (getDelegate() != null) {
				getDelegate().pushedSave(this);
			}
			
		}else if (e.getSource() == saveAsMenuItem) {
			if (getDelegate() != null) {
				getDelegate().pushedSaveAs(this);
			}
			
		}else if (e.getSource() == undoMenuItem) {
			if (getDelegate() != null) {
				getDelegate().pushedUndo(this);
			}
			
		}else if (e.getSource() == redoMenuItem) {
			if (getDelegate() != null) {
				getDelegate().pushedRedo(this);
			}
			
		}else if (e.getSource() == findMenuItem) {
			
		}else if (e.getSource() == replaceMenuItem) {
			
		}else if (e.getSource() == generalViewMenuItem) {
			setSelectedView(0);
			
		}else if (e.getSource() == translationsViewMenuItem) {
			setSelectedView(1);
			
		}else if (e.getSource() == minimizeMenuItem) {
			setState(JFrame.ICONIFIED);
			
		}else if (e.getSource() == zoomMenuItem) {
			if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				
			}else{
				setExtendedState(JFrame.NORMAL);
			}
			
		}else if (e.getSource() == helpMenuItem) {
			
		}else if (e.getSource() == generalButton) {
			setSelectedView(0);
			
		}else if (e.getSource() == translationsButton) {
			setSelectedView(1);
			
		}else if (e.getSource() == addLanguageButton) {
			if (getDataSource() != null) {
				getDataSource().addLanguage(this);
			}
			
		}else if (e.getSource() == removeLanguageButton) {
			if (getDataSource() != null) {
				getDataSource().removeLanguage(this);
			}
			
		}else if (e.getSource() == addEntryButton) {
			if (getDataSource() != null) {
				getDataSource().addEntry(this);
			}
			
		}else if (e.getSource() == removeEntryButton) {
			if (getDataSource() != null) {
				getDataSource().removeEntry(this);
			}
		}
	}
	
	//ComponentListener:
	public void componentResized(ComponentEvent e) {
		if (e.getSource() == this) {
			updatePositioning();
		}
	}
	
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	
	//FocusListener:
	public void focusGained(FocusEvent e) {
		
	}
	
	public void focusLost(FocusEvent e) {
		
	}

	//HierarchyBoundsListner:
	public void ancestorMoved(HierarchyEvent e) {}
	
	public void ancestorResized(HierarchyEvent e) {
		if (e.getSource() == this) {
			updatePositioning();
		}
	}
	
	//KeyListener:
	public void keyTyped(KeyEvent e) {}
	
	public void keyPressed(KeyEvent e) {
		if (e.getSource() instanceof JTextField && 
			(e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER)) {
			requestFocusInWindow();
		}
	}
	
	public void keyReleased(KeyEvent e) {}
	
	//MouseListener:
	public void mouseClicked(MouseEvent e) {
		
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	//WindowListener:
	public void windowOpened(WindowEvent e) {}
	
	public void windowClosing(WindowEvent e) {
		if (e.getSource() == this) {
			if (getDelegate() != null) {
				getDelegate().pushedClose(this);
			}
		}
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	//Interfaces:
	public interface DocumentWindowDataSource {
		
		public String getTitle(DocumentWindow window);
		
		public String getProjectFolder(DocumentWindow window);
		public void setProjectFolder(DocumentWindow window, String projectFolder);
		
		public String getLanguageFile(DocumentWindow window);
		public void setLanguageFile(DocumentWindow window, String languageFile);
		
		public void addLanguage(DocumentWindow window);
		public void removeLanguage(DocumentWindow window);
		
		public void addEntry(DocumentWindow window);
		public void removeEntry(DocumentWindow window);
		
	}
	
	public interface DocumentWindowDelegate {
		
		public void pushedNew(DocumentWindow window);
		public void pushedOpen(DocumentWindow window);
		
		public void pushedClose(DocumentWindow window);
		public void pushedSave(DocumentWindow window);
		public void pushedSaveAs(DocumentWindow window);
		
		public boolean canUndo(DocumentWindow window);
		public boolean canRedo(DocumentWindow window);
		
		public void pushedUndo(DocumentWindow window);
		public void pushedRedo(DocumentWindow window);
		
		public boolean canRemoveLanguage(DocumentWindow window);
		public boolean canRemoveEntry(DocumentWindow window);
		
	}
	
}
