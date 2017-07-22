package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

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
	private JMenu windowMenu;
	private JMenuItem minimizeMenuItem;
	private JMenuItem zoomMenuItem;
	private JMenu helpMenu;
	private JMenuItem helpMenuItem;
	
	private int selectedLanguageIndex;
	
	private JScrollPane languagesScrollPane;
	private JPanel languagesContentPane;
	private JPanel[] languagePanels;
	private JCheckBox[] languageCheckBoxes;
	private JTextField[] languageLabels;
	private JButton addLanguageButton;
	private JButton removeLanguageButton;
	
	public DocumentWindow() {
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
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);
		
		selectedLanguageIndex = -1;
		
		languagesScrollPane = new JScrollPane();
		languagesScrollPane.setBackground(Color.white);
		languagesScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.lightGray));
		languagesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		languagesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		languagesContentPane = new JPanel();
		languagesContentPane.setBackground(Color.white);
		languagesContentPane.setLayout(null);
		languagesContentPane.addMouseListener(this);
		
		languagePanels = new JPanel[0];
		languageCheckBoxes = new JCheckBox[0];
		languageLabels = new JTextField[0];
		
		languagesScrollPane.setViewportView(languagesContentPane);
		
		addLanguageButton = new JButton("Add");
		addLanguageButton.setOpaque(true);
		addLanguageButton.setBackground(Color.white);
		addLanguageButton.setForeground(Color.black);
		addLanguageButton.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.lightGray));
		addLanguageButton.addActionListener(this);
		
		removeLanguageButton = new JButton("Remove");
		removeLanguageButton.setOpaque(true);
		removeLanguageButton.setBackground(Color.white);
		removeLanguageButton.setForeground(Color.black);
		removeLanguageButton.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.lightGray));
		removeLanguageButton.setEnabled(false);
		removeLanguageButton.addActionListener(this);
		
		setIconImage(Icons.appIcon().getImage());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setJMenuBar(menuBar);
		setLayout(null);
		setMinimumSize(new Dimension(640, 360));
		setLocationRelativeTo(null);
		
		add(languagesScrollPane);
		add(addLanguageButton);
		add(removeLanguageButton);
		
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
	public void reloadData() {
		reloadTitle();
		reloadLanguagesList();
	}
	
	public void reloadTitle() {
		String title = (getDataSource() != null) ? getDataSource().getTitle(this) : null;
		
		setTitle(title);
	}
	
	public void reloadLanguagesList() {
		int nLanguages = (getDataSource() != null) ? getDataSource().getNumberOfLanguages(this) : 0;
		
		setNumberOfRowsInLanguagesList(nLanguages);
		
		for (int i = 0; i < nLanguages; i++) {
			boolean active = getDataSource().isLanguageAtIndexActivated(this, i);
			String language = getDataSource().getLanguageAtIndex(this, i);
			
			JCheckBox checkBox = languageCheckBoxes[i];
			JTextField label = languageLabels[i];
			
			checkBox.setSelected(active);
			label.setText(language);
		}
		
		updateLanguagesListPositioning();
	}
	
	public int getIndexOfSelectedLanguage() {
		return selectedLanguageIndex;
	}
	
	public void setIndexOfSelectedLanguage(int index) {
		if (index < -1 || index >= languagePanels.length) {
			throw new IllegalArgumentException();
		}
		
		if (selectedLanguageIndex != -1) {
			JPanel panel = languagePanels[selectedLanguageIndex];
			
			panel.setBackground(Color.white);
		}
		
		selectedLanguageIndex = index;
		
		if (selectedLanguageIndex != -1) {
			JPanel panel = languagePanels[selectedLanguageIndex];
			
			panel.setBackground(Color.lightGray);
		}
		
		removeLanguageButton.setEnabled(selectedLanguageIndex != -1);
	}
	
	//Languages list handling:
	private void setNumberOfRowsInLanguagesList(int n) {
		int m = languagePanels.length;
		
		if (m < n) {
			languagePanels = Arrays.copyOf(languagePanels, n);
			languageCheckBoxes = Arrays.copyOf(languageCheckBoxes, n);
			languageLabels = Arrays.copyOf(languageLabels, n);
			
			for (int i = m; i < n; i++) {
				JPanel panel = new JPanel();
				
				panel.setBackground(Color.white);
				panel.setBorder(null);
				panel.setLayout(null);
				panel.addMouseListener(this);
				
				JCheckBox checkBox = new JCheckBox();
				
				checkBox.setIcon(Icons.uncheckedIcon());
				checkBox.setSelectedIcon(Icons.checkecIcon());
				checkBox.setBorder(null);
				checkBox.addActionListener(this);
				
				JTextField label = new JTextField();
				
				label.setOpaque(false);
				label.setBackground(null);
				label.setForeground(Color.black);
				label.setBorder(null);
				label.addFocusListener(this);
				label.addKeyListener(this);
				
				panel.add(checkBox);
				panel.add(label);
				
				languagesContentPane.add(panel);
				
				languagePanels[i] = panel;
				languageCheckBoxes[i] = checkBox;
				languageLabels[i] = label;
			}
			
		}else if (m > n) {
			for (int i = n; i < m; i++) {
				JPanel panel = languagePanels[i];
				
				languagesContentPane.remove(panel);
				panel.removeAll();
			}
			
			languagePanels = Arrays.copyOf(languagePanels, n);
			languageCheckBoxes = Arrays.copyOf(languageCheckBoxes, n);
			languageLabels = Arrays.copyOf(languageLabels, n);
			
			if (selectedLanguageIndex >= n) {
				selectedLanguageIndex = -1;
			}
		}
	}
	
	//Positioning:
	private void updatePositioning() {
		//int w = getContentPane().getWidth();
		int h = getContentPane().getHeight();
		
		languagesScrollPane.setLocation(0, 0);
		languagesScrollPane.setSize(160, h - 25);
		
		updateLanguagesListPositioning();
		
		addLanguageButton.setLocation(0, h - 25);
		addLanguageButton.setSize(80, 25);
		
		removeLanguageButton.setLocation(80, h - 25);
		removeLanguageButton.setSize(80, 25);
		
		revalidate();
		repaint();
	}
	
	private void updateLanguagesListPositioning() {
		int nLanguages = languagePanels.length;
		
		languagesContentPane.setPreferredSize(new Dimension(160, Math.max(languagesScrollPane.getHeight() - 1, 
		                                                                  nLanguages * 22)));
		
		for (int i = 0; i < nLanguages; i++) {
			JPanel panel = languagePanels[i];
			JCheckBox checkBox = languageCheckBoxes[i];
			JTextField label = languageLabels[i];
			
			panel.setLocation(0, i * 22);
			panel.setSize(160, 22);
			
			checkBox.setLocation(1, 1);
			checkBox.setSize(20, 20);
			
			if (!label.isFocusOwner()) {
				label.setLocation(22, 1);
				label.setSize(Math.min(137, label.getPreferredSize().width), 20);
			}else{
				label.setLocation(22, 1);
				label.setSize(137, 20);
			}
		}
		
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
			
		}else if (e.getSource() == minimizeMenuItem) {
			setState(JFrame.ICONIFIED);
			
		}else if (e.getSource() == zoomMenuItem) {
			if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				
			}else{
				setExtendedState(JFrame.NORMAL);
			}
			
		}else if (e.getSource() == helpMenuItem) {
			
		}else if (e.getSource() == addLanguageButton) {
			if (getDataSource() != null) {
				getDataSource().addLanguage(this);
			}
			
		}else if (e.getSource() == removeLanguageButton) {
			if (getDataSource() != null) {
				getDataSource().removeLanguage(this);
			}
			
		}else{
			for (int i = 0; i < languagePanels.length; i++) {
				if (e.getSource() == languageCheckBoxes[i]) {
					setIndexOfSelectedLanguage(i);
					
					if (getDataSource() == null) {
						reloadLanguagesList();
					}else{
						getDataSource().setLanguageAtIndexActivated(this, i, languageCheckBoxes[i].isSelected());
					}
					
					return;
				}
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
		for (int i = 0; i < languagePanels.length; i++) {
			if (e.getSource() == languageLabels[i]) {
				languageLabels[i].setSize(137, 20);
				
				setIndexOfSelectedLanguage(i);
				
				return;
			}
		}
	}
	
	public void focusLost(FocusEvent e) {
		for (int i = 0; i < languagePanels.length; i++) {
			if (e.getSource() == languageLabels[i]) {
				JTextField label = languageLabels[i];
				
				label.setSize(Math.min(137, label.getPreferredSize().width), 20);
				
				if (getDataSource() == null) {
					reloadLanguagesList();
				}else{
					getDataSource().setLanguageAtIndex(this, i, label.getText());
				}
				
				return;
			}
		}
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
		if (e.getSource() == languagesContentPane) {
			setIndexOfSelectedLanguage(-1);
			
			requestFocusInWindow();
			
		}else{
			for (int i = 0; i < languagePanels.length; i++) {
				if (e.getSource() == languagePanels[i]) {
					setIndexOfSelectedLanguage(i);
					
					return;
				}
			}
		}
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
		
		public int getNumberOfLanguages(DocumentWindow window);
		public boolean isLanguageAtIndexActivated(DocumentWindow window, int index);
		public String getLanguageAtIndex(DocumentWindow window, int index);
		
		public void setLanguageAtIndexActivated(DocumentWindow window, int index, boolean activated);
		public void setLanguageAtIndex(DocumentWindow window, int index, String language);
		
		public void addLanguage(DocumentWindow window);
		public void removeLanguage(DocumentWindow window);
		/*
		public int getNumberOfEntries(DocumentWindow window);
		public int getNumberOfDisplayedLanguaged(DocumentWindow window);
		public String getDisplayLanguageAtIndex(DocumentWindow window, int index);
		public String getKeyOfEntryAtIndex(DocumentWindow window, int index);
		public String getTranslationForLanguageOfEntryAtIndex(DocumentWindow window, int entryIndex, int languageIndex);
		*/
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
		
	}
	
}
