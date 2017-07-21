package ui;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import model.*;

import nanoTranslation.*;

import ui.DocumentWindow.*;

public class DocumentWindowController implements DocumentWindowDataSource, 
												 DocumentWindowDelegate {
	private File file;
	private Document originalDocument;
	private Document document;
	
	private DocumentWindow window;
	
	private HashMap<String, Boolean> activeLanguages;
	
	public DocumentWindowController() {
		file = null;
		originalDocument = new Document();
		document = originalDocument.clone();
		
		activeLanguages = new HashMap<>();
		
		for (int i = 0; i < document.getTranslations().getNumberOfLanguages(); i++) {
			activeLanguages.put(document.getTranslations().getLanguageAtIndex(i), Boolean.TRUE);
		}
		
		window = new DocumentWindow();
		
		window.setDataSource(this);
		window.setDelegate(this);
		
		window.setVisible(true);
	}
	
	public DocumentWindowController(File f) {
		file = f;
		originalDocument = new Document(file);
		document = originalDocument.clone();
		
		activeLanguages = new HashMap<>();
		
		for (int i = 0; i < document.getTranslations().getNumberOfLanguages(); i++) {
			activeLanguages.put(document.getTranslations().getLanguageAtIndex(i), Boolean.TRUE);
		}
		
		window = new DocumentWindow();
		
		window.setDataSource(this);
		window.setDelegate(this);
		
		window.setVisible(true);
	}
	
	//DocumentWindow DataSource & Delegate:
	public String getTitle(DocumentWindow window) {
		String name = "Untitled";
		
		if (file != null) {
			name = file.getName();
		}
		
		return "NanoTranslation - " + name;
	}
	
	public int getNumberOfLanguages(DocumentWindow window) {
		return document.getTranslations().getNumberOfLanguages();
	}
	
	public boolean isLanguageAtIndexActivated(DocumentWindow window, int index) {
		return activeLanguages.get(document.getTranslations().getLanguageAtIndex(index)).booleanValue();
	}
	
	public String getLanguageAtIndex(DocumentWindow window, int index) {
		return document.getTranslations().getLanguageAtIndex(index);
	}
	
	public void setLanguageAtIndexActivated(DocumentWindow window, int index, boolean activated) {
		activeLanguages.put(document.getTranslations().getLanguageAtIndex(index), new Boolean(activated));
		
		window.reloadData();
	}
	
	public void setLanguageAtIndex(DocumentWindow window, int index, String language) {
		String oldLanguage = document.getTranslations().getLanguageAtIndex(index);
		
		if (oldLanguage.equals(language)) {
			return;
		}else if (language == null || language.equals("")) {
			JOptionPane.showMessageDialog(window, 
                                          "Empty names are not allowed.", 
                                          "Error", 
                                          JOptionPane.OK_OPTION, 
                                          Icons.appIcon());
			window.reloadData();
			
			return;
			
		}else if (document.getTranslations().getIndexOfLanguage(language) != -1) {
			JOptionPane.showMessageDialog(window, 
                                          "There is already a language with this name.", 
                                          "Error", 
                                          JOptionPane.OK_OPTION, 
                                          Icons.appIcon());
			window.reloadData();
			
			return;
		}
		
		document.getTranslations().setLanguageAtIndex(index, language);
		
		activeLanguages.put(language, activeLanguages.remove(oldLanguage));
		
		window.reloadData();
		window.setIndexOfSelectedLanguage(document.getTranslations().getIndexOfLanguage(language));
	}
	
	public void addLanguage(DocumentWindow window) {
		String newLanguage = null;
		
		while (newLanguage == null) {
			newLanguage = (String) JOptionPane.showInputDialog(window, 
			                                                   "New language:", 
			                                                   "", 
			                                                   JOptionPane.QUESTION_MESSAGE, 
			                                                   Icons.appIcon(), 
			                                                   null, 
			                                                   null);
			
			if (newLanguage == null) {
				return;
				
			}else if (newLanguage.equals("")) {
				int result = JOptionPane.showConfirmDialog(window, 
				                                           "Empty names are not allowed.", 
				                                           "Naming Error", 
				                                           JOptionPane.OK_CANCEL_OPTION, 
				                                           JOptionPane.ERROR_MESSAGE, 
				                                           Icons.appIcon());
				
				if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}else{
					newLanguage = null;
				}
				
			}else if (document.getTranslations().getIndexOfLanguage(newLanguage) != -1) {
				int result = JOptionPane.showConfirmDialog(window, 
				                                           "There is already a language with this name.", 
				                                           "Naming Error", 
				                                           JOptionPane.OK_CANCEL_OPTION, 
				                                           JOptionPane.ERROR_MESSAGE, 
				                                           Icons.appIcon());
				
				if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}else{
					newLanguage = null;
				}
			}
		}
		
		document.getTranslations().addLanguage(newLanguage);
		
		activeLanguages.put(newLanguage, Boolean.TRUE);
		
		window.reloadData();
		window.setIndexOfSelectedLanguage(document.getTranslations().getIndexOfLanguage(newLanguage));
	}
	
	public void removeLanguage(DocumentWindow window) {
		int index = window.getIndexOfSelectedLanguage();
		
		if (index == -1) {
			return;
		}
		
		String oldLanguage = document.getTranslations().getLanguageAtIndex(index);
		
		int result = JOptionPane.showConfirmDialog(window, 
		                                           "Are you sure you want to delete " + oldLanguage + "?\n" + 
		                                           "This deletes all translations for this language.", 
		                                           "Deleting Language", 
		                                           JOptionPane.OK_CANCEL_OPTION, 
		                                           JOptionPane.WARNING_MESSAGE, 
		                                           Icons.appIcon());
		if (result != JOptionPane.OK_OPTION) {
			return;
		}
		
		document.getTranslations().removeLanguage(index);
		
		activeLanguages.remove(oldLanguage);
		
		window.reloadData();
		window.setIndexOfSelectedLanguage(-1);
	}
	
	public void pushedNew(DocumentWindow window) {
		NanoTranslation.newWindowController();
	}
	
	public void pushedOpen(DocumentWindow window) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open File");
		fileChooser.setFileHidingEnabled(true);
		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("NanoTranslation File (*.ntf)", "ntf"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setApproveButtonText("Open");
		
		int result = fileChooser.showOpenDialog(null);
		
		if (result == JFileChooser.CANCEL_OPTION || 
			result == JFileChooser.ERROR_OPTION) {
			return;
		}
		
		File f = fileChooser.getSelectedFile();
		
		if (file == null && 
			document.equals(originalDocument)) {
			file = f;
			originalDocument = new Document(file);
			document = originalDocument.clone();
			
			activeLanguages.clear();
			
			for (int i = 0; i < document.getTranslations().getNumberOfLanguages(); i++) {
				activeLanguages.put(document.getTranslations().getLanguageAtIndex(i), Boolean.TRUE);
			}
			
			window.reloadData();
			
		}else{
			NanoTranslation.newWindowController(f);			
		}
	}
	
	public void pushedClose(DocumentWindow window) {
		if (originalDocument.equals(document) == false) {
			int result = JOptionPane.showConfirmDialog(window, 
			                                           "Do you want to save changes?", 
			                                           "Unsaved Changes", 
			                                           JOptionPane.YES_NO_CANCEL_OPTION, 
			                                           JOptionPane.WARNING_MESSAGE, 
			                                           Icons.appIcon());
			if (result == JOptionPane.CANCEL_OPTION) {
				return;
			}else if (result == JOptionPane.YES_OPTION) {
				if (file == null) {
					JFileChooser fileChooser = new JFileChooser();
					
					fileChooser.setDialogTitle("Save File");
					fileChooser.setFileHidingEnabled(true);
					fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
					fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("NanoTranslation File (*.ntf)", "ntf"));
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setMultiSelectionEnabled(false);
					fileChooser.setApproveButtonText("Open");
					
					result = fileChooser.showSaveDialog(null);
					
					if (result == JFileChooser.CANCEL_OPTION || 
						result == JFileChooser.ERROR_OPTION) {
						return;
					}
					
					file = fileChooser.getSelectedFile();
				}
				
				document.writeToFile(file);
			}
		}
		
		window.dispose();
		
		NanoTranslation.removeWindowController(this);
	}
	
	public void pushedSave(DocumentWindow window) {
		if (file == null) {
			showSaveDialog();
			
		}else{
			document.writeToFile(file);
			
			originalDocument = document;
			document = originalDocument.clone();
			
			window.reloadData();
		}
	}
	
	public void pushedSaveAs(DocumentWindow window) {
		showSaveDialog();
	}
	
	private void showSaveDialog() {
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setDialogTitle("Save File");
		fileChooser.setFileHidingEnabled(true);
		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("NanoTranslation File (*.ntf)", "ntf"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setApproveButtonText("Open");
		
		int result = fileChooser.showSaveDialog(null);
		
		if (result == JFileChooser.CANCEL_OPTION || 
			result == JFileChooser.ERROR_OPTION) {
			return;
		}
		
		file = fileChooser.getSelectedFile();
		
		document.writeToFile(file);
		
		originalDocument = document;
		document = originalDocument.clone();
		
		window.reloadData();
	}
	
	public boolean canUndo(DocumentWindow window) {
		return false;
	}
	
	public boolean canRedo(DocumentWindow window) {
		return false;
	}
	
	public void pushedUndo(DocumentWindow window) {
		
	}
	
	public void pushedRedo(DocumentWindow window) {
		
	}
	
}
