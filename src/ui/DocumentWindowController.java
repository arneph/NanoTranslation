package ui;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import model.*;

import nanoTranslation.*;

import ui.DocumentWindow.*;
import ui.ListView.*;
import ui.TableView.*;

public class DocumentWindowController implements DocumentWindowDataSource, 
												 DocumentWindowDelegate, 
												 ListViewDataSource, 
												 ListViewDelegate, 
												 TableViewDataSource, 
												 TableViewDelegate {
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
		
		window = new DocumentWindow();
		
		window.setDataSource(this);
		window.setDelegate(this);
		
		window.getLanguagesListView().setDataSource(this);
		window.getLanguagesListView().setDelegate(this);
		
		window.getLanguageSelectionListView().setDataSource(this);
		window.getLanguageSelectionListView().setDelegate(this);
		
		window.getEntriesTableView().setDataSource(this);
		window.getEntriesTableView().setDelegate(this);
		
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
		
		window.getLanguagesListView().setDataSource(this);
		window.getLanguagesListView().setDelegate(this);
		
		window.getLanguageSelectionListView().setDataSource(this);
		window.getLanguageSelectionListView().setDelegate(this);
		
		window.getEntriesTableView().setDataSource(this);
		window.getEntriesTableView().setDelegate(this);
		
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
	
	public String getProjectFolder(DocumentWindow window) {
		return document.getProjectFolderPath();
	}
	
	public void setProjectFolder(DocumentWindow window, String projectFolder) {
		document.setProjectFolderPath(projectFolder);
		
		window.reloadData();
	}
	
	public String getLanguageFile(DocumentWindow window) {
		return document.getLanguageFilePath();
	}
	
	public void setLanguageFile(DocumentWindow window, String languageFile) {
		document.setLanguageFilePath(languageFile);
		
		window.reloadData();
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
		
		window.getLanguagesListView().reloadData();
		window.getLanguagesListView().setSelectedRow(document.getTranslations().getIndexOfLanguage(newLanguage));
		
		window.getLanguageSelectionListView().reloadData();
		window.getEntriesTableView().reloadData();
	}
	
	public void removeLanguage(DocumentWindow window) {
		int index = window.getLanguagesListView().getSelectedRow();
		
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
		
		window.getLanguagesListView().reloadData();
		window.getLanguagesListView().setSelectedRow(-1);
		
		window.getLanguageSelectionListView().reloadData();
		window.getEntriesTableView().reloadData();
	}
	
	public void addEntry(DocumentWindow window) {
		String newKey = null;
		
		while (newKey == null) {
			newKey = (String) JOptionPane.showInputDialog(window, 
			                                                "New key:", 
			                                                "", 
			                                                JOptionPane.QUESTION_MESSAGE, 
			                                                Icons.appIcon(), 
			                                                null, 
			                                                null);
			
			if (newKey == null) {
				return;
				
			}else if (newKey.equals("")) {
				int result = JOptionPane.showConfirmDialog(window, 
				                                           "Empty keys are not allowed.", 
				                                           "Naming Error", 
				                                           JOptionPane.OK_CANCEL_OPTION, 
				                                           JOptionPane.ERROR_MESSAGE, 
				                                           Icons.appIcon());
				
				if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}else{
					newKey = null;
				}
				
			}else if (document.getTranslations().getIndexOfKeyOfEntry(newKey) != -1) {
				int result = JOptionPane.showConfirmDialog(window, 
				                                           "There is already a key with this name.", 
				                                           "Naming Error", 
				                                           JOptionPane.OK_CANCEL_OPTION, 
				                                           JOptionPane.ERROR_MESSAGE, 
				                                           Icons.appIcon());
				
				if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}else{
					newKey = null;
				}
			}
		}
		
		document.getTranslations().addEntry(newKey);
		
		window.getEntriesTableView().reloadData();
		window.getEntriesTableView().setSelectedCell(0, document.getTranslations().getIndexOfKeyOfEntry(newKey));
	}
	
	public void removeEntry(DocumentWindow window) {
		int index = window.getEntriesTableView().getSelectedRow();
		
		if (index == -1) {
			return;
		}
		
		document.getTranslations().removeEntry(index);
		
		window.getEntriesTableView().reloadData();
		window.getEntriesTableView().setSelectedCell(-1, -1);
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
			window.getLanguagesListView().reloadData();
			window.getLanguageSelectionListView().reloadData();
			window.getEntriesTableView().reloadData();
			
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
	
	public boolean canRemoveLanguage(DocumentWindow window) {
		return window.getLanguagesListView().getSelectedRow() != -1;
	}
	
	public boolean canRemoveEntry(DocumentWindow window) {
		return window.getEntriesTableView().getSelectedRow() != -1;
	}
	
	//ListView DataSource & Delegate:
	public int getNumberOfRows(ListView listView) {
		if (listView == window.getLanguagesListView()) {
			return document.getTranslations().getNumberOfLanguages();
			
		}else if (listView == window.getLanguageSelectionListView()) {
			return document.getTranslations().getNumberOfLanguages();
			
		}else{
			return 0;
		}
	}
	
	public boolean isRowChecked(ListView listView, int index) {
		if (listView == window.getLanguagesListView()) {
			return false;
			
		}else if (listView == window.getLanguageSelectionListView()) {
			return activeLanguages.get(document.getTranslations().getLanguageAtIndex(index));
			
		}else{
			return false;
		}
	}
	
	public void setRowChecked(ListView listView, int index, boolean checked) {
		if (listView == window.getLanguageSelectionListView()) {
			activeLanguages.put(document.getTranslations().getLanguageAtIndex(index), checked);
			
			window.getLanguageSelectionListView().reloadData();
			window.getEntriesTableView().reloadData();
		}
	}
	
	public String getRowValue(ListView listView, int index) {
		if (listView == window.getLanguagesListView()) {
			return document.getTranslations().getLanguageAtIndex(index);
			
		}else if (listView == window.getLanguageSelectionListView()) {
			return document.getTranslations().getLanguageAtIndex(index);
			
		}else{
			return "";
		}
	}
	
	public void setRowValue(ListView listView, int index, String value) {
		if (listView == window.getLanguagesListView()) {
			String newLanguage = value;
			String oldLanguage = document.getTranslations().getLanguageAtIndex(index);
			
			if (oldLanguage.equals(newLanguage)) {
				return;
			}else if (newLanguage == null || newLanguage.equals("")) {
				JOptionPane.showMessageDialog(window, 
	                                          "Empty names are not allowed.", 
	                                          "Error", 
	                                          JOptionPane.OK_OPTION, 
	                                          Icons.appIcon());
				window.reloadData();
				
				return;
				
			}else if (document.getTranslations().getIndexOfLanguage(newLanguage) != -1) {
				JOptionPane.showMessageDialog(window, 
	                                          "There is already a language with this name.", 
	                                          "Error", 
	                                          JOptionPane.OK_OPTION, 
	                                          Icons.appIcon());
				window.reloadData();
				
				return;
			}
			
			document.getTranslations().setLanguageAtIndex(index, newLanguage);
			
			activeLanguages.put(newLanguage, activeLanguages.remove(oldLanguage));
			
			window.getLanguagesListView().reloadData();
			window.getLanguagesListView().setSelectedRow(document.getTranslations().getIndexOfLanguage(newLanguage));
			
			window.getLanguageSelectionListView().reloadData();
			window.getEntriesTableView().reloadData();
		}
	}
	
	public boolean shouldShowCheckBoxes(ListView listView) {
		if (listView == window.getLanguagesListView()) {
			return false;
			
		}else if (listView == window.getLanguageSelectionListView()) {
			return true;
			
		}else{
			return false;
		}
	}
	
	public boolean shouldAllowSelection(ListView listView) {
		if (listView == window.getLanguagesListView()) {
			return true;
			
		}else if (listView == window.getLanguageSelectionListView()) {
			return false;
			
		}else{
			return true;
		}
	}
	
	public boolean shouldAllowEditing(ListView listView) {
		if (listView == window.getLanguagesListView()) {
			return true;
			
		}else if (listView == window.getLanguageSelectionListView()) {
			return false;
			
		}else{
			return true;
		}
	}
	
	public void selectionChanged(ListView listView) {
		window.reloadData();
	}
	
	public int getNumberOfColumns(TableView tableView) {
		if (tableView == window.getEntriesTableView()) {
			return 2 + document.getTranslations().getNumberOfLanguages();
			
		}else{
			return 0;
		}
	}
	
	public int getNumberOfRows(TableView tableView) {
		if (tableView == window.getEntriesTableView()) {
			return document.getTranslations().getNumberOfEntries();
			
		}else{
			return 0;
		}
	}
	
	public String getColumnTitle(TableView tableView, int columnIndex) {
		if (tableView == window.getEntriesTableView()) {
			if (columnIndex == 0) {
				return "Key";
				
			}else if (columnIndex <= document.getTranslations().getNumberOfLanguages()) {
				return document.getTranslations().getLanguageAtIndex(columnIndex - 1);
			
			}else{
				return "Information";
			}
			
		}else{
			return null;
		}
	}
	
	public String getCellValue(TableView tableView, int columnIndex, int rowIndex) {
		if (tableView == window.getEntriesTableView()) {
			if (columnIndex == 0) {
				return document.getTranslations().getKeyOfEntryAtIndex(rowIndex);
				
			}else if (columnIndex <= document.getTranslations().getNumberOfLanguages()) {
				return document.getTranslations().getTranslationForLanguageOfEntryAtIndex(rowIndex, columnIndex - 1);
				
			}else{
				return document.getTranslations().getInformationOfEntryAtIndex(rowIndex);
			}
			
		}else{
			return null;
		}
	}
	
	public void setCellValue(TableView tableView, int columnIndex, int rowIndex, String value) {
		if (tableView == window.getEntriesTableView()) {
			if (columnIndex == 0) {
				document.getTranslations().setKeyOfEntryAtIndex(rowIndex, value);
				
			}else if (columnIndex <= document.getTranslations().getNumberOfLanguages()) {
				document.getTranslations().setTranslationForLanguageOfEntryAtIndex(rowIndex, columnIndex - 1, value);
				
			}else{
				document.getTranslations().setInformationOfEntryAtIndex(rowIndex, value);
			}
			
			window.getEntriesTableView().reloadData();
		}
	}
	
	public void selectionChanged(TableView tableView) {
		window.reloadData();
	}
	
}
