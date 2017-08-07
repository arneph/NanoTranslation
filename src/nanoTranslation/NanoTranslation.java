package nanoTranslation;

import java.io.*;
import java.util.*;

import ui.*;

public class NanoTranslation {
	private static HashSet<DocumentWindowController> windowControllers;
	
	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		Icons.loadIcons();
		
		windowControllers = new HashSet<>();
		
		newWindowController();
	}
	
	public static void openFile(File file) {
		if (file == null || 
			file.exists() == false || 
			file.isFile() == false) {
			throw new IllegalArgumentException();
		}
		
		for (DocumentWindowController windowController : windowControllers) {
			File f = windowController.getFile();
			
			if (file.equals(f)) {
				windowController.makeActive();
				
				return;
				
			}else if (f == null && 
					  windowController.isActive() && 
					  windowController.hasUnsavedChanged() == false) {
				windowController.setFile(file);
				windowController.makeActive();
				
				return;
			}
		}
		
		DocumentWindowController windowController = new DocumentWindowController();
		
		windowController.setFile(file);
		windowController.makeActive();
		
		windowControllers.add(windowController);
	}
	
	public static void newWindowController() {
		windowControllers.add(new DocumentWindowController());
	}
	
	public static void removeWindowController(DocumentWindowController windowController) {
		windowControllers.remove(windowController);
		
		if (windowControllers.isEmpty()) {
			System.exit(0);
		}
	}
	
}
